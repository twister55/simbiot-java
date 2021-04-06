package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;

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
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExpressionsResolver {
    private final FunctionCompiler compiler;
    private final Dispatcher dispatcher;

    public ExpressionsResolver() {
        this.compiler = new FunctionCompiler(this);
        this.dispatcher = new Dispatcher(this);
    }

    public List<Chunk> resolve(CompilerContext ctx, Expression[] expressions) {
        List<Chunk> result = new ArrayList<>();
        for (Expression expression : expressions) {
            result.add(resolve(ctx, expression));
        }
        return result;
    }

    public Chunk resolve(CompilerContext ctx, Expression expression) {
        final Chunk result = new Chunk();
        expression.accept(createVisitor(ctx, result));
        return result;
    }

    protected ExpressionVisitor createVisitor(CompilerContext ctx, Chunk result) {
        return new Visitor(ctx, result);
    }

    protected class Visitor extends ExpressionVisitor {
        private final CompilerContext ctx;
        private final Chunk result;

        public Visitor(CompilerContext ctx, Chunk result) {
            this.ctx = ctx;
            this.result = result;
        }

        @Override
        public void visit(CallExpression expression) {
            result.append(dispatcher.dispatch(ctx, expression));
        }

        @Override
        public void visit(Identifier expression) {
            final String name = expression.getName();

            if ("undefined".equals(name)) {
                result.append(Chunk.NULL);
                return;
            }

            if ("@writer".equals(name)) {
                result.append(ctx.writer());
                return;
            }

            if ("@props".equals(name)) {
                result.append(ctx.props());
                return;
            }

            if ("@slots".equals(name)) {
                result.append(ctx.slots());
                return;
            }

            if ("@empty-slot".equals(name)) {
                result.append(Chunk.forField(FunctionCompiler.emptySlot()));
                return;
            }

            result.append(ctx.resolve(expression.getName()));
        }

        @Override
        public void visit(Literal expression) {
            if (expression.isNull()) {
                result.append(Chunk.NULL);
            } else if (expression.isBoolean()) {
                result.append(Chunk.of(IntegerConstant.forValue(expression.isTrue()), boolean.class));
            } else if (expression.isNumber()) {
                result.append(Chunk.of(IntegerConstant.forValue(expression.getInt()), int.class));
            } else {
                result.append(Chunk.of(new TextConstant(expression.getString()), String.class));
            }
        }

        @Override
        public void visit(ObjectExpression expression) {
            List<Expression> args = new ArrayList<>();
            for (Property property : expression) {
                final String name = property.getKey().getName();

                args.add(new Literal(name));
                args.add(property.getValue());
            }
            visit(new CallExpression("@object", args));
        }

        @Override
        public void visit(ArrayExpression expression) {
            visit(new CallExpression("@array", expression.getElements()));
        }

        @Override
        public void visit(MemberExpression expression) {
            final Expression obj = expression.getObject();

            if (obj instanceof Identifier) {
                final String name = ((Identifier) obj).getName();
                if ("#ctx".equals(name)) {
                    result.append(ctx.resolve(expression.getLeadingComments()[0].getValue()));
                    return;
                }
            }

            final Chunk objChunk = resolve(ctx, obj);
            if (objChunk.type().isArray()) {
                final Literal index = (Literal) expression.getProperty();
                result.append(objChunk.result());
                result.append(IntegerConstant.forValue(index.getInt()));
                result.append(ArrayAccess.REFERENCE.load(), objChunk.type().getComponentType());
                return;
            }

            visit(new CallExpression("@access", expression.getObject(), new Literal(((Identifier) expression.getProperty()).getName())));
        }

        @Override
        public void visit(ArrowFunctionExpression expression) {
            final ScopedContext scopedCtx = ctx.createInner();
            final TypeDescription type = compiler.compile(scopedCtx, expression);

            result.append(Chunk.of(
                new StackManipulation.Compound(
                    TypeCreation.of(type),
                    Duplication.SINGLE,
                    ctx.writer().result(),
                    ctx.props().result(),
                    ctx.slots().result(),
                    scopedCtx.vars(ctx::resolve),
                    ctx.resolve("@components").result(),
                    MethodInvocation.invoke(type.getDeclaredMethods().filter(isConstructor()).getOnly())
                ),
                type.asGenericType()
            ));
        }
    }
}
