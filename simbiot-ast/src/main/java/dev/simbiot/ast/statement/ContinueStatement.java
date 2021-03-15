package dev.simbiot.ast.statement;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ContinueStatement extends BaseNode implements Statement {
    private final Identifier label;

    @JsonCreator
    public ContinueStatement(@Nullable @JsonProperty("label") Identifier label) {
        super("ContinueStatement");
        this.label = label;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Identifier getLabel() {
        return label;
    }
}
