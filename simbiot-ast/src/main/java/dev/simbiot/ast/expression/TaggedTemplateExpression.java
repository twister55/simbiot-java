package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TaggedTemplateExpression extends BaseNode implements Expression {
    private final Expression tag;
    private final TemplateLiteral quasi;

    @JsonCreator
    public TaggedTemplateExpression(@JsonProperty("tag") Expression tag,
                                    @JsonProperty("quasi") TemplateLiteral quasi) {
        super("TaggedTemplateExpression");
        this.tag = tag;
        this.quasi = quasi;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getTag() {
        return tag;
    }

    public TemplateLiteral getQuasi() {
        return quasi;
    }
}
