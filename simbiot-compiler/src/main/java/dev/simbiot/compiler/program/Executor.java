package dev.simbiot.compiler.program;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.expression.ArrayExpression;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.compiler.CompilerContext;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Executor {
    private final Dispatcher dispatcher;

    public Executor() {
        this.dispatcher = new Dispatcher(this);
    }

    public List<Chunk> execute(CompilerContext ctx, Expression[] expressions) {
        List<Chunk> result = new ArrayList<>();
        for (Expression expression : expressions) {
            result.add(execute(ctx, expression));
        }
        return result;
    }

    public Chunk execute(CompilerContext ctx, Expression expression) {
        final Chunk result = new Chunk();
        expression.accept(new Visitor(ctx, result));
        return result;
    }

    private class Visitor extends ExpressionVisitor {
        private final CompilerContext ctx;
        private final Chunk result;

        private Visitor(CompilerContext ctx, Chunk result) {
            this.ctx = ctx;
            this.result = result;
        }

        @Override
        public void visit(CallExpression expression) {
            result.append(dispatcher.dispatch(ctx, expression));
        }

        @Override
        public void visit(Identifier expression) {
            result.append(ctx.resolve(expression.getName()));
        }

        @Override
        public void visit(Literal expression) {
            if (expression.isNull()) {
                result.append(Chunk.NULL);
            } else if (expression.isBoolean()) {
                result.append(expression.isTrue());
            } else if (expression.isNumber()) {
                result.append(expression.getInt());
            } else {
                result.append(expression.getString());
            }
        }

        @Override
        public void visit(ObjectExpression expression) {
            List<Expression> args = new ArrayList<>();
            for (Property property : expression) {
                args.add(new Literal(property.getKey().getName()));
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
            if (acceptSpecific(expression) || acceptTyped(expression)) {
                return;
            }

            visit(new CallExpression("@access", expression.getObject(), new Literal(((Identifier) expression.getProperty()).getName())));
        }

        private boolean acceptSpecific(MemberExpression expression) {
            final Expression obj = expression.getObject();

            if (obj instanceof Identifier) {
                final String name = ((Identifier) obj).getName();

                if ("#ctx".equals(name)) {
                    result.append(ctx.resolve(expression.getLeadingComments()[0].getValue()));
                    return true;
                }

                if ("arguments".equals(name)) {
                    result.append(ctx.argument(((Literal) expression.getProperty()).getInt()));
                    return true;
                }
            }

            return false;
        }

        private boolean acceptTyped(MemberExpression expression) {
            final Chunk chunk = execute(ctx, expression.getObject());
            if (chunk.type().isArray()) {
                final Literal index = (Literal) expression.getProperty();
                result.append(chunk.build());
                result.append(IntegerConstant.forValue(index.getInt()));
                result.append(ArrayAccess.REFERENCE.load(), chunk.type().getComponentType());
                return true;
            }

            return false;
        }
    }
}
