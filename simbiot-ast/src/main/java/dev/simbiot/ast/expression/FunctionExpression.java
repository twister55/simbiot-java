package dev.simbiot.ast.expression;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.statement.BlockStatement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class FunctionExpression extends BaseFunction implements Expression {
    private final Identifier id;
    private final BlockStatement body;

    public FunctionExpression(@JsonProperty("id") Identifier id,
                              @JsonProperty("body") BlockStatement body,
                              @JsonProperty("params") Pattern[] params,
                              @JsonProperty("params") boolean generator,
                              @JsonProperty("params") boolean async) {
        super("FunctionExpression", params, generator, async);
        this.id = id;
        this.body = body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Identifier getId() {
        return id;
    }

    public BlockStatement getBody() {
        return body;
    }
}
