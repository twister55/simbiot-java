package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class BlockStatement extends BaseNode implements Statement {
    private final Statement[] body;

    @JsonCreator
    public BlockStatement(@JsonProperty("body") Statement[] body) {
        this("BlockStatement", body);
    }

    protected BlockStatement(String type, Statement[] body) {
        super(type);
        this.body = body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Statement[] getBody() {
        return body;
    }
}
