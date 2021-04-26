package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.Component.Slot;
import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.ArrayExpression;
import dev.simbiot.ast.expression.ArrowFunctionExpression;
import dev.simbiot.ast.expression.BinaryExpression;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.ConditionalExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.expression.UnaryExpression;
import dev.simbiot.ast.expression.UpdateExpression;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfEqual;
import dev.simbiot.compiler.bytecode.IfFalse;
import dev.simbiot.compiler.bytecode.JumpTarget;
import dev.simbiot.compiler.bytecode.PrimitiveBoxing;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.InGenericShape;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.Label;
import static dev.simbiot.compiler.BuiltIn.COMPONENTS_FIELD_NAME;
import static dev.simbiot.compiler.BuiltIn.CONSTANTS_FIELD_NAME;
import static dev.simbiot.compiler.BuiltIn.PROPS;
import static dev.simbiot.compiler.BuiltIn.SLOTS;
import static dev.simbiot.compiler.BuiltIn.WRITER;
import static dev.simbiot.compiler.BuiltIn.WRITER_WRITE;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.implementation.bytecode.collection.ArrayFactory.forType;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.INTEGER;
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

    public StackChunk resolve(CompilerContext ctx, Expression expression) {
        final StackChunk result = new StackChunk();
        expression.accept(createVisitor(ctx, result));
        return result;
    }

    protected StackChunk resolve(Literal expression) {
        if (expression.isNull()) {
            return StackChunk.NULL;
        }

        if (expression.isBoolean()) {
            return new StackChunk(boolean.class, IntegerConstant.forValue(expression.isTrue()));
        }

        if (expression.isNumber()) {
            return new StackChunk(int.class, IntegerConstant.forValue(expression.getInt()));
        }

        return new StackChunk(String.class, new TextConstant(expression.getString()));
    }

    protected StackChunk resolve(CompilerContext ctx, Identifier expression) {
        switch (expression.getName()) {
            case "undefined":
                return StackChunk.NULL;

            case "@writer":
                return ctx.writer();

            case "@props":
                return ctx.props();

            case "@slots":
                return ctx.slots();

            case "@empty-slot":
                return StackChunk.forField(
                    new ForLoadedType(Slot.class).getDeclaredFields().filter(named("EMPTY")).getOnly()
                );

            default:
                return ctx.resolve(expression.getName());
        }
    }

    protected StackChunk resolve(CompilerContext ctx, CallExpression expression) {
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

    protected StackChunk resolve(CompilerContext ctx, ObjectExpression expression) {
        List<Expression> args = new ArrayList<>();
        for (Property property : expression) {
            args.add(new Literal(property.getKey().getName()));
            args.add(property.getValue());
        }
        return resolve(ctx, new CallExpression("@object", args));
    }

    protected StackChunk resolve(CompilerContext ctx, ArrayExpression expression) {
        return resolve(ctx, new CallExpression("@array", expression.getElements()));
    }

    protected StackChunk resolve(CompilerContext ctx, MemberExpression expression) {
        final StackChunk obj = resolve(ctx, expression.getObject());
        final Expression property = expression.getProperty();

        if (obj.type().isArray()) {
            final Literal index = (Literal) property;
            return new StackChunk(
                obj.type().getComponentType(),
                new Compound(obj, IntegerConstant.forValue(index.getInt()), ArrayAccess.REFERENCE.load())
            );
        }

        final Expression key = property instanceof Literal ? property : new Literal(((Identifier) property).getName());
        return resolve(ctx, new CallExpression("@access", expression.getObject(), key));
    }

    protected StackChunk resolve(CompilerContext ctx, ArrowFunctionExpression expression) {
        final InlineFunctionContext scopedCtx = ctx.createInlineContext();
        final TypeDescription type = compiler.compile(scopedCtx, expression);

        return new StackChunk(
            type.asGenericType(),
            new Compound(
                TypeCreation.of(type),
                Duplication.SINGLE,
                resolve(ctx, WRITER),
                resolve(ctx, PROPS),
                resolve(ctx, SLOTS),
                scopedCtx.vars(ctx::resolve),
                resolve(ctx, new Identifier("@components")),
                MethodInvocation.invoke(type.getDeclaredMethods().filter(isConstructor()).getOnly())
            )
        );
    }

    protected StackChunk resolve(CompilerContext ctx, BinaryExpression expression) {
        StackChunk left = resolve(ctx, expression.getLeft());
        StackChunk right = resolve(ctx, expression.getRight());

        switch (expression.getOperator()) {
            case "==":
            case "===":
                if (left.type().isPrimitive() && left.type().equals(right.type())) {
                    final Label ifLabel = new Label();
                    final Label elseLabel = new Label();

                    return new StackChunk()
                        .append(left)
                        .append(right)
                        .append(new IfEqual(int.class, ifLabel))
                        .append(IntegerConstant.forValue(false))
                        .append(new GoTo(elseLabel))
                        .append(new JumpTarget(ifLabel))
                        .append(IntegerConstant.forValue(true))
                        .append(new JumpTarget(elseLabel), boolean.class);
                }

                return invoke(new StackChunk(), StackChunk.EQUALS, resolve(ctx, new Expression[] {
                    expression.getLeft(), expression.getRight()
                }));

            case "!=":
            case "!==":
                if (left.type().isPrimitive() && left.type().equals(right.type())) {
                    final Label ifLabel = new Label();
                    final Label elseLabel = new Label();

                    return new StackChunk()
                        .append(left)
                        .append(right)
                        .append(new IfEqual(int.class, ifLabel))
                        .append(IntegerConstant.forValue(true))
                        .append(new GoTo(elseLabel))
                        .append(new JumpTarget(ifLabel))
                        .append(IntegerConstant.forValue(false))
                        .append(new JumpTarget(elseLabel), boolean.class);
                }

                return StackChunk.negation(invoke(new StackChunk(), StackChunk.EQUALS, resolve(ctx, new Expression[] {
                    expression.getLeft(), expression.getRight()
                })));

            default:
                throw new UnsupportedNodeException(expression, "binary expression");
        }
    }

    protected StackChunk resolve(CompilerContext ctx, UnaryExpression expression) {
        if ("!".equals(expression.getOperator())) {
            return StackChunk.negation(resolve(ctx, expression.getArgument()));
        }

        throw new UnsupportedNodeException(expression, "unary expression");
    }

    protected StackChunk resolve(CompilerContext ctx, UpdateExpression expression) {
        final int offset = ctx.offset(expression.getArgument().getName());

        switch (expression.getOperator()) {
            case INCREMENT:
                return new StackChunk(int.class, INTEGER.increment(offset, 1));

            case DECREMENT:
                return new StackChunk(int.class, INTEGER.increment(offset, -1));

            default:
                throw new UnsupportedNodeException(expression, "update expression");
        }
    }

    protected StackChunk resolve(CompilerContext ctx, ConditionalExpression expression) {
        final Label ifLabel = new Label();
        final Label elseLabel = new Label();

        return StackChunk.condition(resolve(ctx, expression.getTest()))
            .append(new IfFalse(ifLabel))
            .append(resolve(ctx, expression.getConsequent()))
            .append(new GoTo(elseLabel))
            .append(new JumpTarget(ifLabel))
            .append(resolve(ctx, expression.getAlternate()))
            .append(new JumpTarget(elseLabel));
    }

    protected StackChunk dispatch(CompilerContext ctx, Identifier callee, Expression[] args) {
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

        return invoke(new StackChunk(), bindings.get(name), resolve(ctx, args));
    }

    protected StackChunk dispatch(CompilerContext ctx, MemberExpression callee, Expression[] args) {
        final StackChunk result = resolve(ctx, callee.getObject());
        final Identifier methodId = (Identifier) callee.getProperty();
        final List<StackChunk> arguments = resolve(ctx, args);
        final MethodList<InGenericShape> methods = result.type()
            .getDeclaredMethods()
            .filter(named(methodId.getName()));

        if (methods.size() == 1) {
            return invoke(result, methods.getOnly(), arguments);
        }

        MethodList<InGenericShape> filtered = methods.filter(takesArguments(args.length));
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

    protected StackChunk invoke(StackChunk callee, MethodDescription method, List<StackChunk> arguments) {
        if (method.isVarArgs()) {
            callee.append(forType(of(Object.class).asGenericType()).withValues(arguments));
        } else {
            for (final StackChunk arg : arguments) {
                callee.append(arg);
            }
        }

        return callee.append(MethodInvocation.invoke(method), method.getReturnType());
    }

    protected List<StackChunk> resolve(CompilerContext ctx, Expression[] expressions) {
        List<StackChunk> result = new ArrayList<>();
        for (Expression expression : expressions) {
            final StackChunk value = resolve(ctx, expression);
            if (value.type().isPrimitive()) {
                value.append(PrimitiveBoxing.of(value.type()), value.type());
            }
            result.add(value);
        }
        return result;
    }

    protected ExpressionVisitor createVisitor(CompilerContext ctx,  StackChunk result) {
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
                result.append(resolve(expression));
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

            @Override
            public void visit(BinaryExpression expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(UnaryExpression expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(UpdateExpression expression) {
                result.append(resolve(ctx, expression));
            }

            @Override
            public void visit(ConditionalExpression expression) {
                result.append(resolve(ctx, expression));
            }
        };
    }
}
