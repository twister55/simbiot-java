package dev.simbiot.ast.statement;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ForStatement extends BaseNode implements Statement {
    private final Expression init;
    private final Expression test;
    private final Expression update;
    private final Statement body;

    @JsonCreator
    public ForStatement(@JsonProperty("body") Statement body,
                        @Nullable @JsonProperty("init") Expression init,
                        @Nullable @JsonProperty("test") Expression test,
                        @Nullable @JsonProperty("update") Expression update) {
        super("ForStatement");
        this.init = init;
        this.test = test;
        this.update = update;
        this.body = body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Expression getInit() {
        return init;
    }

    @Nullable
    public Expression getTest() {
        return test;
    }

    @Nullable
    public Expression getUpdate() {
        return update;
    }

    public Statement getBody() {
        return body;
    }
}
