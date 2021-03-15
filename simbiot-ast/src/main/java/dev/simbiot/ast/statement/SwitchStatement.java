package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SwitchStatement extends BaseNode implements Statement {
    private final Expression discriminant;
    private final SwitchCase[] cases;

    @JsonCreator
    public SwitchStatement(@JsonProperty("discriminant") Expression discriminant,
                           @JsonProperty("cases") SwitchCase[] cases) {
        super("SwitchStatement");
        this.discriminant = discriminant;
        this.cases = cases;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getDiscriminant() {
        return discriminant;
    }

    public SwitchCase[] getCases() {
        return cases;
    }
}
