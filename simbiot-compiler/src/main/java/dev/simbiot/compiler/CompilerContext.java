package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.REFERENCE;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.of;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CompilerContext {
    private final String id;
    private final Map<String, Integer> offsets;
    private final Map<String, Generic> types;
    private final List<String> constants;
    private final List<String> componentIds;
    private final List<Unloaded<?>> inlineTypes;

    private TypeDescription type;
    private ParameterList<?> parameters;
    private int offset;

    public CompilerContext(String id) {
        this.id = id;
        this.offsets = new HashMap<>();
        this.types = new HashMap<>();
        this.constants = new ArrayList<>();
        this.componentIds = new ArrayList<>();
        this.inlineTypes = new ArrayList<>();
    }

    protected CompilerContext(String id, CompilerContext parent) {
        this.id = id;
        this.offsets = new HashMap<>();
        this.types = new HashMap<>();
        this.constants = parent.constants;
        this.componentIds = parent.componentIds;
        this.inlineTypes = parent.inlineTypes;
        this.type = parent.type;
    }

    public void bind(TypeDescription type, ParameterList<?> parameters) {
        this.type = type;
        this.parameters = parameters;
        this.offset = parameters.size();
    }

    public String getId() {
        return id;
    }

    public TypeDescription type() {
        return type;
    }

    public int getStackSize() {
        return offset + 1;
    }

    public StackChunk writer() {
        return param(0);
    }

    public StackChunk props() {
        return param(1);
    }

    public StackChunk slots() {
        return param(2);
    }

    public StackChunk resolve(String name) {
        if (name.startsWith("@")) {
            return field(name.substring(1));
        } else {
            return localVar(name);
        }
    }

    public StackManipulation declare(String name, StackChunk value) {
        final int offset = ++this.offset;
        types.put(name, value.type());
        offsets.put(name, offset);
        return new Compound(
            value,
            of(value.type()).storeAt(offset)
        );
    }

    public int offset(String name) {
        final Integer offset = offsets.get(name);
        if (offset == null) {
            return -1;
        }
        return offset;
    }

    public List<String> getConstants() {
        return constants;
    }

    public int addConstant(Literal text) {
        final int index = constants.size();
        constants.add(text.getString());
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

    public void addInlineType(Unloaded<?> type) {
        inlineTypes.add(type);
    }

    public List<Unloaded<?>> getInlineTypes() {
        return inlineTypes;
    }

    public List<TypeDescription> getDeclaredTypes() {
        return inlineTypes.stream().map(DynamicType::getTypeDescription).collect(Collectors.toList());
    }

    public InlineFunctionContext createInlineContext() {
        return new InlineFunctionContext(this, inlineTypes.size());
    }

    protected StackChunk localVar(String name) {
        final Generic type = types.getOrDefault(name, Generic.OBJECT);
        final int offset = offset(name);

        if (offset == -1) {
            return StackChunk.NULL;
        }

        if (type.isPrimitive()) {
            return new StackChunk(
                type,
                new Compound(of(type).loadFrom(offset)/*, PrimitiveBoxing.forPrimitive(type)*/)
            );
        }

        return new StackChunk(type, of(type).loadFrom(offset));
    }

    protected StackChunk field(String name) {
        FieldList<InDefinedShape> fields = type.getDeclaredFields().filter(named(name));

        if (fields.isEmpty()) {
            TypeDescription enclosingType = type.getEnclosingType();

            while (enclosingType != null) {
                // FIXME figure out how to access outer class fields (i.e. Outer.this.components)
                fields = enclosingType.getDeclaredFields().filter(named(name).and(isStatic()));

                if (!fields.isEmpty()) {
                    break;
                }

                enclosingType = enclosingType.getEnclosingType();
            }
        }

        return fields.isEmpty() ? StackChunk.NULL : StackChunk.forField(fields.getOnly());
    }

    protected StackChunk param(int index) {
        final ParameterDescription param = this.parameters.get(index);
        return new StackChunk(param.getType(), REFERENCE.loadFrom(param.getOffset()));
    }
}
