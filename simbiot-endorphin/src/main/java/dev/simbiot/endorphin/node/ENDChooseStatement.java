package dev.simbiot.endorphin.node;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Program;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDChooseStatement extends BaseNode implements ENDNode {
    /**
     * Added internally by expression hoister: contains expression for picking
     * one of inner `<choose>` statement
     */
    @Nullable
    private final Program test;
    private final ENDChooseCase[] cases;

    @JsonCreator
    public ENDChooseStatement(@Nullable @JsonProperty("test") Program test,
                              @JsonProperty("cases") ENDChooseCase[] cases) {
        super("ENDChooseStatement");
        this.test = test;
        this.cases = cases;
    }

    public Program getTest() {
        return test;
    }

    public ENDChooseCase[] getCases() {
        return cases;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
