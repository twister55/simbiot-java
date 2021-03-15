package dev.simbiot.ast.statement;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SwitchCase extends BaseNode {
    @Nullable
    private final Expression test;
    private final Statement[] consequent;

    @JsonCreator
    public SwitchCase(@JsonProperty("test") Expression test,
                      @JsonProperty("consequent") Statement[] consequent) {
        super("SwitchCase");
        this.test = test;
        this.consequent = consequent;
    }

    @Nullable
    public Expression getTest() {
        return test;
    }

    public Statement[] getConsequent() {
        return consequent;
    }
}
