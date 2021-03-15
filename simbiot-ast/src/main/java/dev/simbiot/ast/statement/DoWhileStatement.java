package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class DoWhileStatement extends BaseNode implements Statement {
    private final Statement body;
    private final Expression test;

    @JsonCreator
    public DoWhileStatement(@JsonProperty("body") Statement body,
                            @JsonProperty("test") Expression test) {
        super("DoWhileStatement");
        this.body = body;
        this.test = test;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Statement getBody() {
        return body;
    }

    public Expression getTest() {
        return test;
    }
}
