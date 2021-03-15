package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class LabeledStatement extends BaseNode implements Statement {
    private final Statement body;
    private final Identifier label;

    @JsonCreator
    public LabeledStatement(@JsonProperty("body") Statement body,
                            @JsonProperty("label") Identifier label) {
        super("LabeledStatement");
        this.body = body;
        this.label = label;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Statement getBody() {
        return body;
    }

    public Identifier getLabel() {
        return label;
    }
}

