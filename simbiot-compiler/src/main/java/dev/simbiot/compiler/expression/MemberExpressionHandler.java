package dev.simbiot.compiler.expression;

import java.util.Arrays;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class MemberExpressionHandler implements ExpressionHandler<MemberExpression> {

    /**
     * The {@link Runtime#access(Object, Object)} method.
     */
    private static final InDefinedShape OBJECT_ACCESS = new ForLoadedType(Runtime.class)
        .getDeclaredMethods()
        .filter(named("object"))
        .getOnly();

    @Override
    public StackChunk handle(CompilerContext ctx, MemberExpression expression) {
        final StackChunk obj = ctx.resolve(expression.getObject());

        if (obj.type().isArray()) {
            return arrayAccess(obj, (Literal) expression.getProperty());
        }

        return objectAccess(ctx, obj, expression.getProperty());
    }

    private StackChunk arrayAccess(StackChunk obj, Literal index) {
        return new StackChunk()
            .append(obj)
            .append(IntegerConstant.forValue(index.getInt()))
            .append(ArrayAccess.REFERENCE.load())
            .as(obj.type().getComponentType());
    }

    private StackChunk objectAccess(CompilerContext ctx, StackChunk obj, Expression property) {
        final Literal key = property instanceof Literal ?
            (Literal) property :
            new Literal(((Identifier) property).getName());

        return StackChunk.call(OBJECT_ACCESS, Arrays.asList(obj, ctx.resolve(key)));
    }
}
