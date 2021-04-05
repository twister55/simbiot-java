package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.REFERENCE;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CompilerContext {
    private final String id;
    private final Map<String, Chunk> refs;
    private final Map<String, Generic> types;
    private final List<StackManipulation> constants;
    private final List<String> componentIds;
    private final List<Unloaded<?>> inlineTypes;

    private TypeDescription type;
    private ParameterList<?> parameters;
    private int localVarsCount;
    private int offset;

    public CompilerContext(String id) {
        this.id = id;
        this.refs = new HashMap<>();
        this.types = new HashMap<>();
        this.constants = new ArrayList<>();
        this.componentIds = new ArrayList<>();
        this.inlineTypes = new ArrayList<>();
    }

    protected CompilerContext(String id, CompilerContext parent) {
        this.id = id;
        this.refs = new HashMap<>();
        this.types = new HashMap<>();
        this.constants = parent.constants;
        this.componentIds = parent.componentIds;
        this.inlineTypes = parent.inlineTypes;
        this.type = parent.type;
    }

    public void init(TypeDescription type, ParameterList<?> parameters) {
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

    public int getLocalVarsCount() {
        return localVarsCount;
    }

    public Chunk writer() {
        return param(0);
    }

    public Chunk props() {
        return param(1);
    }

    public Chunk slots() {
        return param(2);
    }

    public Chunk resolve(String name) {
        if (name.startsWith("@")) {
            return field(name.substring(1));
        } else {
            return ref(name);
        }
    }

    public StackManipulation store(String name, Chunk value) {
        final int ref = ++offset;
        localVarsCount++;
        types.put(name, value.type());
        refs.put(name, Chunk.of(REFERENCE.loadFrom(offset), types.get(name)));
        return new Compound(value.result(), REFERENCE.storeAt(ref));
    }

    public List<StackManipulation> getConstants() {
        return constants;
    }

    public int addConstant(String text) {
        final int index = constants.size();
        constants.add(new Compound(
            new TextConstant(text),
            MethodInvocation.invoke(
                new ForLoadedType(String.class)
                    .getDeclaredMethods()
                    .filter(named("getBytes").and(takesArguments(0)))
                    .getOnly()
            )
        ));
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

    public ScopedContext createInner() {
        return new ScopedContext(this, inlineTypes.size());
    }

    protected Chunk param(int index) {
        final ParameterDescription param = this.parameters.get(index);
        return Chunk.of(REFERENCE.loadFrom(param.getOffset()), param.getType());
    }

    protected Chunk ref(String name) {
        return refs.getOrDefault(name, Chunk.NULL);
    }

    protected Chunk field(String name) {
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

        return fields.isEmpty() ? Chunk.NULL : Chunk.forField(fields.getOnly());
    }
}
