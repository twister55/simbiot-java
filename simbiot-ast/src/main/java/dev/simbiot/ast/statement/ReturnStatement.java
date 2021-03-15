package dev.simbiot.ast.statement;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ReturnStatement extends BaseNode implements Statement {
    private final Expression argument;

    @JsonCreator
    public ReturnStatement(@Nullable @JsonProperty("argument") Expression argument) {
        super("ReturnStatement");
        this.argument = argument;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Expression getArgument() {
        return argument;
    }
}
