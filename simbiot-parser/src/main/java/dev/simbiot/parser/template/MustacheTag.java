package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class MustacheTag extends BaseNode implements TemplateNode {
    private final Expression expression;

    @JsonCreator
    public MustacheTag(@JsonProperty("expression") Expression expression) {
        this("MustacheTag", expression);
    }

    protected MustacheTag(String type, Expression expression) {
        super(type);
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return expression;
    }
}
