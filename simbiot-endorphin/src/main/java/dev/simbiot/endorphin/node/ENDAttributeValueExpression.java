package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

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

    public PlainStatement[] getElements() {
        return elements;
    }
}
