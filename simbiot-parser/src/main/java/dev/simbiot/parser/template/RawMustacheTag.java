package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class RawMustacheTag extends MustacheTag {

    @JsonCreator
    public RawMustacheTag(@JsonProperty("expression") Expression expression) {
        super("RawMustacheTag", expression);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
