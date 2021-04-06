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
public class ENDIfStatement extends BaseNode implements TemplateNode {
    private final Program test;
    private final TemplateNode[] consequent;

    @JsonCreator
    public ENDIfStatement(@JsonProperty("test") Program test,
                          @JsonProperty("consequent") TemplateNode[] consequent) {
        super("ENDIfStatement");
        this.test = test;
        this.consequent = consequent;
    }

    public Expression getTest() {
        return ((ExpressionStatement) test.getBody()[0]).getExpression();
    }

    public TemplateNode[] getConsequent() {
        return consequent;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
