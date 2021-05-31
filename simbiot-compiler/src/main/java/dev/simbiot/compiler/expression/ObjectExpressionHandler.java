package dev.simbiot.compiler.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ObjectExpressionHandler implements ExpressionHandler<ObjectExpression> {

    /**
     * The {@link Collections#emptyMap()} method.
     */
    private static final InDefinedShape EMPTY_MAP = new ForLoadedType(Collections.class)
        .getDeclaredMethods()
        .filter(named("emptyMap"))
        .getOnly();

    /**
     * The {@link Runtime#object(Object...)} method.
     */
    private static final InDefinedShape CREATE_OBJECT = new ForLoadedType(Runtime.class)
        .getDeclaredMethods()
        .filter(named("object"))
        .getOnly();

    @Override
    public StackChunk handle(CompilerContext ctx, ObjectExpression expression) {
        if (expression.isEmpty()) {
            return StackChunk.call(EMPTY_MAP);
        }

        return StackChunk.call(CREATE_OBJECT, getProperties(ctx, expression));
    }

    private List<StackChunk> getProperties(CompilerContext ctx, ObjectExpression expression) {
        List<Expression> result = new ArrayList<>();
        for (Property property : expression) {
            result.add(toLiteral(property.getKey()));
            result.add(property.getValue());
        }
        return ctx.resolve(result);
    }
    
    private Literal toLiteral(Expression expression) {
        if (expression instanceof Identifier) {
            return new Literal(((Identifier) expression).getName());
        }
        
        return (Literal) expression;
    }
}
