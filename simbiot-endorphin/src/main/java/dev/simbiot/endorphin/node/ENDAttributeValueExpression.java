package dev.simbiot.endorphin.node;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDAttributeValueExpression extends BaseNode implements PlainStatement {
    private final PlainStatement[] elements;

    @JsonCreator
    public ENDAttributeValueExpression(@JsonProperty("elements") PlainStatement[] elements) {
        super("ENDAttributeValueExpression");
        this.elements = elements;
    }

    @Override
    public Expression getExpression() {
        List<Expression> arguments = new ArrayList<>();
        for (PlainStatement element : elements) {
            arguments.add(element.getExpression());
        }
        return new CallExpression("@concat", arguments);
    }
}
