package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class MemberExpressionHandler implements ExpressionHandler<MemberExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, MemberExpression expression) {
        final StackChunk obj = ctx.resolve(expression.getObject());
        final Expression property = expression.getProperty();

        if (obj.type().isArray()) {
            final Literal index = (Literal) property;
            return new StackChunk(
                obj.type().getComponentType(),
                new StackManipulation.Compound(obj, IntegerConstant.forValue(index.getInt()), ArrayAccess.REFERENCE.load())
            );
        }

        final Expression key = property instanceof Literal ? property : new Literal(((Identifier) property).getName());
        return ctx.resolve(new CallExpression("@access", expression.getObject(), key));
    }
}
