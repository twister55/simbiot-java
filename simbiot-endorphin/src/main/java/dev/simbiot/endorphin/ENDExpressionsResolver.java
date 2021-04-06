package dev.simbiot.endorphin;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.Chunk;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.ExpressionsResolver;
import dev.simbiot.endorphin.node.expression.IdentifierNode;
import dev.simbiot.endorphin.node.expression.IdentifierNode.Context;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDExpressionsResolver extends ExpressionsResolver {

    @Override
    protected ExpressionVisitor createVisitor(CompilerContext ctx, Chunk result) {
        return new Visitor(ctx, result);
    }

    protected class Visitor extends ExpressionsResolver.Visitor {

        public Visitor(CompilerContext ctx, Chunk result) {
            super(ctx, result);
        }

        @Override
        public void visit(Identifier expression) {
            if (expression instanceof IdentifierNode) {
                final Context context = ((IdentifierNode) expression).getContext();

                if (context == Context.PROPERTY) {
                    visit(new CallExpression("@attr", new Literal(expression.getName())));
                    return;
                }
            }

            super.visit(expression);
        }
    }
}
