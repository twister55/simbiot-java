package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDAttribute extends BaseNode {
    private final AttributeName name;
    private final PlainStatement value;

    public ENDAttribute(@JsonProperty("name") AttributeName name,
                        @JsonProperty("value") PlainStatement value) {
        super("ENDAttribute");
        this.name = name;
        this.value = value;
    }

    public AttributeName getName() {
        return name;
    }

    public Expression getValue() {
        return value.getExpression();
    }
}
