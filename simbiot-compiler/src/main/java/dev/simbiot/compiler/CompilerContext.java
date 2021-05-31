package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.simbiot.Function;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.FunctionExpression;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.compiler.bytecode.PrimitiveBoxing;
import dev.simbiot.compiler.bytecode.StackChunk;
import dev.simbiot.compiler.expression.ExpressionResolver;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.REFERENCE;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.of;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CompilerContext {
    private final String id;
    private final MethodBindings bindings;
    private final ExpressionResolver resolver;
    private final FunctionCompiler compiler;
    private final Map<String, Integer> offsets;
    private final Map<String, Generic> types;
    private final List<String> constants;
    private final List<String> componentIds;
    private final List<Unloaded<?>> inlineTypes;

    private TypeDescription type;
    private ParameterList<?> parameters;
    private int offset;

    public CompilerContext(String id, MethodBindings bindings, ExpressionResolver resolver) {
        this.id = id;
        this.bindings = bindings;
        this.resolver = resolver;
        this.compiler = new FunctionCompiler();
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
        this.bindings = parent.bindings;
        this.resolver = parent.resolver;
        this.compiler = parent.compiler;
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

    public TypeDescription compile(FunctionExpression expression) {
        final Unloaded<Function> unloaded = compiler.compile(this, expression);
        inlineTypes.add(unloaded);
        return unloaded.getTypeDescription();
    }

    public StackChunk call(String name, Expression[] args) {
        if (!name.startsWith("@")) {
            final StackChunk var = localVar(name);
            if (var != StackChunk.NULL) {
                return var.as(Function.class).invoke("apply", resolve(args));
            }
        }

        final MethodDescription bindMethod = bindings.get(name);
        if (bindMethod != null) {
            return StackChunk.call(bindMethod, resolve(args));
        }

        throw new IllegalStateException(name + " is not defined");
    }

    public StackChunk param(int index) {
        final ParameterDescription param = this.parameters.get(index);
        return new StackChunk(param.getType(), REFERENCE.loadFrom(param.getOffset()));
    }

    public StackChunk resolve(String name) {
        if (name.startsWith("@")) {
            return field(name.substring(1));
        } else {
            return localVar(name);
        }
    }

    public StackChunk resolve(Expression expression) {
        return resolver.resolve(this, expression);
    }

    public List<StackChunk> resolve(Expression... expressions) {
        return resolve(Arrays.asList(expressions));
    }

    public List<StackChunk> resolve(List<Expression> expressions) {
        List<StackChunk> result = new ArrayList<>();
        for (Expression expression : expressions) {
            final StackChunk value = resolve(expression);
            if (value.type().isPrimitive()) {
                value.append(PrimitiveBoxing.of(value.type()), value.type());
            }
            result.add(value);
        }
        return result;
    }

    public StackManipulation declare(VariableDeclarator declarator) {
        final String name = declarator.getId().getName();
        final StackChunk value = resolve(declarator.getInit());
        final int offset = ++this.offset;
        types.put(name, value.type());
        offsets.put(name, offset);
        return new Compound(value, of(value.type()).storeAt(offset));
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

    public List<Unloaded<?>> getInlineTypes() {
        return inlineTypes;
    }

    public List<TypeDescription> getDeclaredTypes() {
        return inlineTypes.stream().map(DynamicType::getTypeDescription).collect(Collectors.toList());
    }

    public FunctionContext createInlineContext() {
        return new FunctionContext(this, inlineTypes.size());
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

        return fields.isEmpty() ? StackChunk.NULL : forField(fields.getOnly());
    }

    private StackChunk forField(FieldDescription field) {
        return new StackChunk(
            field.getType(),
            new Compound(
                field.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(),
                FieldAccess.forField(field).read()
            )
        );
    }

}
