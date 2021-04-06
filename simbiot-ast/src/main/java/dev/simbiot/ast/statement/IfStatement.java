package dev.simbiot.ast.statement;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class IfStatement extends BaseNode implements Statement {
    private final Expression test;
    private final Statement consequent;
    private final Statement alternate;

    public IfStatement(Expression test, Statement consequent) {
        this(test, consequent, null);
    }

    @JsonCreator
    public IfStatement(@JsonProperty("test") Expression test,
                       @JsonProperty("consequent") Statement consequent,
                       @Nullable @JsonProperty("alternate") Statement alternate) {
        super("IfStatement");
        this.test = test;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getTest() {
        return test;
    }

    public Statement getConsequent() {
        return consequent;
    }

    @Nullable
    public Statement getAlternate() {
        return alternate;
    }
}
