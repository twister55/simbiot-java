package dev.simbiot.compiler.expression;

import java.util.Arrays;
import java.util.Collections;

import dev.simbiot.ast.expression.ArrayExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ArrayExpressionHandler implements ExpressionHandler<ArrayExpression> {

    /**
     * The {@link Collections#emptyList()} method.
     */
    private static final InDefinedShape EMPTY_LIST = new ForLoadedType(Collections.class)
        .getDeclaredMethods()
        .filter(named("emptyList"))
        .getOnly();

    /**
     * The {@link Arrays#asList(Object...)} method.
     */
    private static final InDefinedShape AS_LIST = new ForLoadedType(Arrays.class)
        .getDeclaredMethods()
        .filter(named("asList"))
        .getOnly();

    @Override
    public StackChunk handle(CompilerContext ctx, ArrayExpression expression) {
        if (expression.isEmpty()) {
            return StackChunk.call(EMPTY_LIST);
        }
        return StackChunk.call(AS_LIST, ctx.resolve(expression.getElements()));
    }
}
