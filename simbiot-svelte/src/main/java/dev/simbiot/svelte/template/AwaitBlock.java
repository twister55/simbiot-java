package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class AwaitBlock extends BaseNode implements TemplateNode {
    private final Expression expression;
    private final PendingBlock pendingBlock;
    private final ThenBlock thenBlock;
    private final CatchBlock catchBlock;

    @JsonCreator
    public AwaitBlock(@JsonProperty("expression") Expression expression,
                      @JsonProperty("pending") PendingBlock pendingBlock,
                      @JsonProperty("then") ThenBlock thenBlock,
                      @JsonProperty("catch") CatchBlock catchBlock) {
        super("AwaitBlock");
        this.expression = expression;
        this.pendingBlock = pendingBlock;
        this.thenBlock = thenBlock;
        this.catchBlock = catchBlock;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return expression;
    }

    public PendingBlock getPending() {
        return pendingBlock;
    }

    public ThenBlock getThen() {
        return thenBlock;
    }

    public CatchBlock getCatch() {
        return catchBlock;
    }
}
