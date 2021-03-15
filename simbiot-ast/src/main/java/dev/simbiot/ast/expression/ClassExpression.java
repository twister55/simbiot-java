package dev.simbiot.ast.expression;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.statement.ClassBody;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ClassExpression extends BaseClass implements Expression {
    @Nullable
    private Identifier id;

    public ClassExpression(@Nullable @JsonProperty("id") Identifier id,
                           @JsonProperty("superClass") Expression superClass,
                           @JsonProperty("body") ClassBody body) {
        super("ClassExpression", superClass, body);
        this.id = id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Identifier getId() {
        return id;
    }
}
