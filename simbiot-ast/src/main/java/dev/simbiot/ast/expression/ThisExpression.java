package dev.simbiot.ast.expression;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ThisExpression extends BaseNode implements Expression {

    public ThisExpression() {
        super("ThisExpression");
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
