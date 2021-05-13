package dev.simbiot.compiler.expression;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class ObjectExpressionHandler implements ExpressionHandler<ObjectExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, ObjectExpression expression) {
        List<Expression> args = new ArrayList<>();
        for (Property property : expression) {
            args.add(new Literal(((Identifier)property.getKey()).getName()));
            args.add(property.getValue());
        }
        return ctx.resolve(new CallExpression("@object", args));
    }
}
