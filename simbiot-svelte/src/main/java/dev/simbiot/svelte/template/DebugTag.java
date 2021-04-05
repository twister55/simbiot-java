package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class DebugTag extends BaseNode implements TemplateNode {
    private final Expression[] identifiers;

    @JsonCreator
    public DebugTag(@JsonProperty("identifiers") Expression[] identifiers) {
        super("DebugTag");
        this.identifiers = identifiers;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression[] getIdentifiers() {
        return identifiers;
    }
}
