package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TemplateLiteral extends BaseNode implements Expression {
    private final Expression[] expressions;
    private final TemplateElement[] quasis;

    @JsonCreator
    public TemplateLiteral(@JsonProperty("expressions") Expression[] expressions,
                           @JsonProperty("quasis") TemplateElement[] quasis) {
        super("TemplateLiteral");
        this.expressions = expressions;
        this.quasis = quasis;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression[] getExpressions() {
        return expressions;
    }

    public TemplateElement[] getQuasis() {
        return quasis;
    }
}
