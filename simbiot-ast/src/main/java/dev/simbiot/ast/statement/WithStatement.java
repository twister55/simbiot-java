package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class WithStatement extends BaseNode implements Statement {
    private final Expression object;
    private final Statement body;

    @JsonCreator
    public WithStatement(@JsonProperty("object") Expression object,
                         @JsonProperty("body") Statement body) {
        super("WithStatement");
        this.object = object;
        this.body = body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getObject() {
        return object;
    }

    public Statement getBody() {
        return body;
    }
}
