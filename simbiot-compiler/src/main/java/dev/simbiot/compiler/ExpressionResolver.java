package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.simbiot.Component.Slot;
import dev.simbiot.ast.expression.ArrayExpression;
import dev.simbiot.ast.expression.ArrowFunctionExpression;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.pattern.Property;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import static dev.simbiot.compiler.BuiltIn.COMPONENTS_FIELD_NAME;
import static dev.simbiot.compiler.BuiltIn.CONSTANTS_FIELD_NAME;
import static dev.simbiot.compiler.BuiltIn.PROPS;
import static dev.simbiot.compiler.BuiltIn.SLOTS;
import static dev.simbiot.compiler.BuiltIn.WRITER;
import static dev.simbiot.compiler.BuiltIn.WRITER_WRITE;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.implementation.bytecode.collection.ArrayFactory.forType;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExpressionResolver {
    private final MethodBindings bindings;
    private final FunctionCompiler compiler;

    public ExpressionResolver(MethodBindings bindings) {
        this.bindings = bindings;
        this.compiler = new FunctionCompiler(this);
    }

    public Chunk resolve(CompilerContext ctx, Expression expression) {
        final Chunk result = new Chunk();
        expression.accept(createVisitor(ctx, result));
        return result;
    }

    protected Chunk resolve(CompilerContext ctx, Literal expression) {
        if (expression.isNull()) {
            return Chunk.NULL;
        }

        if (expression.isBoolean()) {
            return Chunk.of(IntegerConstant.forValue(expression.isTrue()), boolean.class);
        }

        if (expression.isNumber()) {
            return Chunk.of(IntegerConstant.forValue(expression.getInt()), int.class);
        }

        return Chunk.of(new TextConstant(expression.getString()), String.class);
    }

    protected Chunk resolve(CompilerContext ctx, Identifier expression) {
        switch (expression.getName()) {
            case "undefined":
                return Chunk.NULL;

            case "@writer":
                return ctx.writer();

            case "@props":
                return ctx.props();

            case "@slots":
                return ctx.slots();

            case "@empty-slot":
                return Chunk.forField(new ForLoadedType(Slot.class).getDeclaredFields().filter(named("EMPTY")).getOnly());

            default:
                return ctx.resolve(expression.getName());
        }
    }

    protected Chunk resolve(CompilerContext ctx, CallExpression expression) {
        final Expression callee = expression.getCallee();
        final Expression[] args = expression.getArguments();

        if (callee instanceof Identifier) {
            return dispatch(ctx, (Identifier) callee, args);
        }

        if (callee instanceof MemberExpression) {
            return dispatch(ctx, (MemberExpression) callee, args);
        }

        throw new IllegalArgumentException(callee.getType() + " as callee is not supported");
    }

    protected Chunk resolve(CompilerContext ctx, ObjectExpression expression) {
        List<Expression> args = new ArrayList<>();
        for (Property property : expression) {
            args.add(new Literal(property.getKey().getName()));
            args.add(property.getValue());
        }
        return resolve(ctx, new CallExpression("@object", args));
    }

    protected Chunk resolve(CompilerContext ctx, ArrayExpression expression) {
        return resolve(ctx, new CallExpression("@array", expression.getElements()));
    }

    protected Chunk resolve(CompilerContext ctx, MemberExpression expression) {
        final Chunk obj = resolve(ctx, expression.getObject());
        final Expression property = expression.getProperty();

        if (obj.type().isArray()) {
            final Literal index = (Literal) property;
            return Chunk.of(
                new StackManipulation.Compound(obj.result(),
                    IntegerConstant.forValue(index.getInt()),
                    ArrayAccess.REFERENCE.load()
                ),
                obj.type().getComponentType()
            );
        }

        final Expression key = property instanceof Literal ? property : new Literal(((Identifier) property).getName());
        return resolve(ctx, new CallExpression("@access", expression.getObject(), key));
    }

    protected Chunk resolve(CompilerContext ctx, ArrowFunctionExpression expression) {
        final ScopedContext scopedCtx = ctx.createInner();
        final TypeDescription type = compiler.compile(scopedCtx, expression);

        return Chunk.of(
            new StackManipulation.Compound(
                TypeCreation.of(type),
                Duplication.SINGLE,
                resolve(ctx, WRITER).result(),
                resolve(ctx, PROPS).result(),
                resolve(ctx, SLOTS).result(),
                scopedCtx.vars(ctx::resolve),
                resolve(ctx, new Identifier("@components")).result(),
                MethodInvocation.invoke(type.getDeclaredMethods().filter(isConstructor()).getOnly())
            ),
            type.asGenericType()
        );
    }

    protected Chunk dispatch(CompilerContext ctx, Identifier callee, Expression[] args) {
        final String name = callee.getName();

        if (name.equals("@write")) {
            Expression value = args[0];
            if (value instanceof Literal) {
                value = new MemberExpression("@" + CONSTANTS_FIELD_NAME, ctx.addConstant((Literal) value));
            }
            return resolve(ctx, new CallExpression(WRITER_WRITE, value));
        }

        if (name.equals("@component")) {
            final int idx = ctx.addComponentId(((Literal) args[0]).getString());
            final MemberExpression newCallee = new MemberExpression(
                new MemberExpression("@" + COMPONENTS_FIELD_NAME, idx),
                new Identifier("render")
            );

            return resolve(ctx, new CallExpression(newCallee, WRITER, args[1], args[2]));
        }

        return invoke(new Chunk(), bindings.get(name), resolve(ctx, args));
    }

    protected Chunk dispatch(CompilerContext ctx, MemberExpression callee, Expression[] args) {
        final Chunk result = resolve(ctx, callee.getObject());
        final Identifier methodId = (Identifier) callee.getProperty();
        final List<Chunk> arguments = resolve(ctx, args);
        final MethodList<MethodDescription.InGenericShape> methods = result.type()
            .getDeclaredMethods()
            .filter(named(methodId.getName()));

        if (methods.size() == 1) {
            return invoke(result, methods.getOnly(), arguments);
        }

        MethodList<MethodDescription.InGenericShape> filtered = methods.filter(takesArguments(args.length));
        if (filtered.size() == 1) {
            return invoke(result, methods.getOnly(), arguments);
        }

        filtered = methods.filter(takesArgument(0, arguments.get(0).type().asErasure()));
        if (filtered.size() == 1) {
            return invoke(result, filtered.getOnly(), arguments);
        }

        filtered = methods.filter(takesArgument(0, Object.class));
        if (filtered.size() == 1) {
            return invoke(result, filtered.getOnly(), arguments);
        }

        throw new IllegalStateException("method " + methodId.getName() + " can not be resolved");
    }

    protected Chunk invoke(Chunk callee, MethodDescription method, List<Chunk> args) {
        List<StackManipulation> arguments = args.stream()
            .map(Chunk::result)
            .collect(Collectors.toList());

        if (method.isVarArgs()) {
            callee.append(forType(of(Object.class).asGenericType()).withValues(arguments));
        } else {
            for (StackManipulation argument : arguments) {
                callee.append(argument);
            }
        }

        return callee.append(MethodInvocation.invoke(method), method.getReturnType());
    }

    protected List<Chunk> resolve(CompilerContext ctx, Expression[] expressions) {
        List<Chunk> result = new ArrayList<>();
        for (Expression expression : expressions) {
            result.add(resolve(ctx, expression));
        }
        return result;
    }

    protected ExpressionVisitor createVisitor(CompilerContext ctx,  Chunk result) {
        return new ExpressionVisitor() {
            @Override
            public void visit(CallExpression expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(Identifier expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(Literal expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(ObjectExpression expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(ArrayExpression expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(MemberExpression expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(ArrowFunctionExpression expression) {
                result.append(resolve(ctx, expression));
            }
        };
    }
}
