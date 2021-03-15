package dev.simbiot.ast.expression;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class YieldExpression extends BaseNode implements Expression {
    @Nullable
    private final Expression argument;
    private final boolean delegate;

    public YieldExpression(@Nullable @JsonProperty("argument") Expression argument,
                           @JsonProperty("delegate") boolean delegate) {
        super("YieldExpression");
        this.argument = argument;
        this.delegate = delegate;

    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Expression getArgument() {
        return argument;
    }

    public boolean isDelegate() {
        return delegate;
    }
}
