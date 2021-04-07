package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Program;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.statement.ExpressionStatement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDIfStatement extends BaseNode implements ENDNode {
    private final Program test;
    private final ENDNode[] consequent;

    @JsonCreator
    public ENDIfStatement(@JsonProperty("test") Program test,
                          @JsonProperty("consequent") ENDNode[] consequent) {
        super("ENDIfStatement");
        this.test = test;
        this.consequent = consequent;
    }

    public Expression getTest() {
        return ((ExpressionStatement) test.getBody()[0]).getExpression();
    }

    public ENDNode[] getConsequent() {
        return consequent;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
