package dev.simbiot.ast.statement.declaration;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.BaseFunction;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.statement.BlockStatement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class FunctionDeclaration extends BaseFunction implements Declaration {
    private final Identifier id;
    private final BlockStatement body;

    @JsonCreator
    public FunctionDeclaration(@JsonProperty("id") Identifier id,
                               @JsonProperty("body") BlockStatement body,
                               @JsonProperty("params") Pattern[] params,
                               @JsonProperty("generator") boolean generator,
                               @JsonProperty("async") boolean async) {
        super("FunctionDeclaration", params, generator, async);
        this.id = id;
        this.body = body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable("It is null when a function declaration is a part of the `export default function` statement")
    public Identifier getId() {
        return id;
    }

    public BlockStatement getBody() {
        return body;
    }
}
