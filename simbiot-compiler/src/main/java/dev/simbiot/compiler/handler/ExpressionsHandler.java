package dev.simbiot.compiler.handler;

import java.util.HashMap;
import java.util.Map;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.Constant;
import dev.simbiot.compiler.bytecode.ObjectAccess;
import dev.simbiot.compiler.handler.call.CallHandler;
import dev.simbiot.compiler.handler.call.ComponentHandler;
import dev.simbiot.compiler.handler.call.ObjectHandler;
import dev.simbiot.compiler.handler.call.StaticHandler;
import dev.simbiot.compiler.handler.call.WriteHandler;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExpressionsHandler implements Handler<Expression> {
    private final Map<String, CallHandler> mapping;

    public ExpressionsHandler() {
        this.mapping = new HashMap<>();
        this.mapping.put("attr", new ObjectHandler(this, "props", "get"));
        this.mapping.put("write", new WriteHandler(this));
        this.mapping.put("component", new ComponentHandler(this));
    }

    public ExpressionsHandler(Map<String, CallHandler> mapping) {
        this.mapping = mapping;
    }

    @Override
    public void handle(CompilerContext ctx, Expression expression, HandleResult result) {
        if (expression == null) {
            return;
        }

        expression.accept(new ExpressionVisitor() {
            @Override
            public void visit(Literal expression) {
                result.append(new Constant(expression));
            }

            @Override
            public void visit(Identifier expression) {
                result.append(ctx.get(expression.getName()));
            }

            @Override
            public void visit(MemberExpression expression) {
                Expression obj = expression.getObject();
                Expression prop = expression.getProperty();

                if (obj instanceof Identifier) {
                    final String name = ((Identifier) obj).getName();

                    if ("#ctx".equals(name)) { // FIXME
                        result.append(ctx.get(expression.getLeadingComments()[0].getValue()));
                    }
                } else {
                    handle(ctx, obj, result);
                    result.append(new ObjectAccess(prop));
                }
            }

            @Override
            public void visit(CallExpression expression) {
                result.append(dispatch(ctx, expression));
            }
        });
    }

    private HandleResult dispatch(CompilerContext ctx, CallExpression call) {
        final HandleResult result = new HandleResult();

        call.getCallee().accept(new ExpressionVisitor() {
            @Override
            public void visit(Identifier expression) {
                getHandler(expression).handle(ctx, call, result);
            }

            @Override
            public void visit(MemberExpression expression) {
                final Identifier obj = (Identifier) expression.getObject();
                final Identifier method = (Identifier) expression.getProperty();

                getHandler(obj, method).handle(ctx, call, result);
            }
        });

        return result;
    }

    private CallHandler getHandler(Identifier obj, Identifier method) {
        return new ObjectHandler(this, obj.getName(), method.getName());
    }

    private CallHandler getHandler(Identifier id) {
        return mapping.computeIfAbsent(id.getName(), method -> new StaticHandler(this, Runtime.class, method));
    }
}
