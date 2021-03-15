package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ForOfStatement extends BaseForXStatement {

    @JsonCreator
    public ForOfStatement(@JsonProperty("left") VariableDeclaration left,
                          @JsonProperty("right") Expression right,
                          @JsonProperty("body") Statement body) {
        super("ForOfStatement", left, right, body);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
