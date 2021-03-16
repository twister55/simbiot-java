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
import static net.bytebuddy.implementation.bytecode.member.FieldAccess.forField;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.REFERENCE;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ProcessContext {
    private final Map<String, Integer> refs;
    private final Map<String, Generic> types;
    private final List<StackManipulation> constants;

    private FieldList<InDefinedShape> fields;
    private int offset;
    private int localVarsCount = 0;

    public ProcessContext() {
        this.refs = new HashMap<>();
        this.types = new HashMap<>();
        this.constants = new ArrayList<>();
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
        final FieldList<InDefinedShape> filter = fields.filter(named(name));
        return filter.isEmpty() ? NullConstant.INSTANCE : forField(filter.getOnly()).read();
    }

    private Generic getFieldType(String name) {
        return fields.filter(named(name)).getOnly().getType();
    }
}
