package dev.simbiot.ast.statement;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class BreakStatement extends BaseNode implements Statement {
    private Identifier label;

    public BreakStatement() {
        super("BreakStatement");
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Identifier getLabel() {
        return label;
    }

    public void setLabel(Identifier label) {
        this.label = label;
    }
}
