package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EventHandler extends Attribute {
    private final String[] modifiers;
    private final Expression expression;

    @JsonCreator
    public EventHandler(@JsonProperty("name") String name,
                        @JsonProperty("value") TemplateNode[] value,
                        @JsonProperty("modifiers") String[] modifiers,
                        @JsonProperty("expression") Expression expression) {
        super("EventHandler", name, value);
        this.modifiers = modifiers;
        this.expression = expression;
    }

    public String[] getModifiers() {
        return modifiers;
    }

    public Expression getExpression() {
        return expression;
    }
}
