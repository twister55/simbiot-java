package dev.simbiot.svelte.template;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.pattern.Pattern;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EachBlock extends Block implements TemplateNode {
    private final Expression expression;
    private final Pattern context;
    private final String index;

    @JsonCreator
    public EachBlock(@JsonProperty("expression") Expression expression,
                     @JsonProperty("context")  Pattern context,
                     @JsonProperty("children") TemplateNode[] children,
                     @Nullable @JsonProperty("index")  String index) {
        super("EachBlock", children);
        this.expression = expression;
        this.context = context;
        this.index = index;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return expression;
    }

    public Pattern getContext() {
        return context;
    }

    @Nullable
    public String getIndex() {
        return index;
    }
}
