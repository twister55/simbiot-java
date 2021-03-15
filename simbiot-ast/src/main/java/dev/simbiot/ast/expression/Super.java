package dev.simbiot.ast.expression;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Super extends BaseNode implements Expression {

    public Super() {
        super("Super");
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
