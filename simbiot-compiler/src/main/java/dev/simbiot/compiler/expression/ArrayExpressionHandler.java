package dev.simbiot.compiler.expression;

import java.util.Arrays;

import dev.simbiot.ast.expression.ArrayExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class ArrayExpressionHandler implements ExpressionHandler<ArrayExpression> {

    /**
     * The {@link Arrays#asList(Object...)} method.
     */
    public static final MethodDescription.InDefinedShape AS_LIST = new TypeDescription.ForLoadedType(Arrays.class)
        .getDeclaredMethods()
        .filter(named("asList"))
        .getOnly();

    @Override
    public StackChunk handle(CompilerContext ctx, ArrayExpression expression) {
        return StackChunk.call(AS_LIST, ctx.resolve(expression.getElements()));
    }
}
