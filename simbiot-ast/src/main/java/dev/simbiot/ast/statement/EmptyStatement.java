package dev.simbiot.ast.statement;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EmptyStatement extends BaseNode implements Statement {

    public EmptyStatement() {
        super("EmptyStatement");
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
