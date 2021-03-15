package dev.simbiot.ast.statement;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class DebuggerStatement extends BaseNode implements Statement {

    public DebuggerStatement() {
        super("DebuggerStatement");
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
