package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class WhileStatement extends BaseNode implements Statement {
    private final Expression test;
    private final Statement body;

    @JsonCreator
    public WhileStatement(@JsonProperty("test") Expression test,
                          @JsonProperty("body") Statement body) {
        super("WhileStatement");
        this.test = test;
        this.body = body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getTest() {
        return test;
    }

    public Statement getBody() {
        return body;
    }
}
