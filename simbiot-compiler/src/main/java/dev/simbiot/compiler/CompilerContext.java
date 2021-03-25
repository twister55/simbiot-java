package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.simbiot.compiler.bytecode.ConstantBytes;
import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.REFERENCE;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CompilerContext {
    public static final String CONSTANTS_FIELD_NAME = "$$CONSTANTS";
    public static final String COMPONENTS_FIELD_NAME = "$$components";

    private final String id;
    private final Map<String, Integer> refs;
    private final Map<String, Generic> types;
    private final List<StackManipulation> constants;
    private final List<String> componentIds;

    private FieldList<InDefinedShape> fields;
    private int offset;
    private int localVarsCount = 0;

    public CompilerContext(String id) {
        this.id = id;
        this.refs = new HashMap<>();
        this.types = new HashMap<>();
        this.constants = new ArrayList<>();
        this.componentIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getLocalVarsCount() {
        return localVarsCount;
    }

    public StackManipulation store(String name) {
        localVarsCount++;
        return REFERENCE.storeAt(storeRef(name));
    }

    public StackManipulation store(String name, Class<?> type) {
        types.put(name, TypeDescription.ForLoadedType.of(type).asGenericType());
        return store(name);
    }

    public StackManipulation store(String name, Generic type) {
        types.put(name, type);
        return store(name);
    }

    public StackManipulation get(String name) {
        if ("undefined".equals(name)) {
            return NullConstant.INSTANCE;
        }

        return getRef(name);
    }

    public Generic getType(String name) {
        return types.computeIfAbsent(name, this::getFieldType);
    }

    public void fields(FieldList<InDefinedShape> fields) {
        this.fields = fields;
    }

    public List<StackManipulation> getConstants() {
        return constants;
    }

    public int addConstant(String text) {
        final int index = constants.size();
        constants.add(new ConstantBytes(text));
        return index;
    }

    public List<String> getComponentIds() {
        return componentIds;
    }

    public int addComponentId(String id) {
        final int index = componentIds.size();
        componentIds.add(id);
        return index;
    }

    private StackManipulation getRef(String name) {
        final Integer offset = refs.get(name);
        return offset != null ? REFERENCE.loadFrom(offset) : getField(name);
    }

    private int storeRef(String name) {
        final int ref = ++offset;
        this.refs.put(name, ref);
        return ref;
    }

    private StackManipulation getField(String name) {
        final InDefinedShape fieldShape = getFieldShape(name);

        if (fieldShape == null) {
            return NullConstant.INSTANCE;
        }

        return new StackManipulation.Compound(
            fieldShape.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(),
            FieldAccess.forField(fieldShape).read()
        );
    }

    private Generic getFieldType(String name) {
        return getFieldShape(name).getType();
    }

    private InDefinedShape getFieldShape(String name) {
        final FieldList<InDefinedShape> filter = fields.filter(named(name));
        return filter.isEmpty() ? null : filter.getOnly();
    }
}
