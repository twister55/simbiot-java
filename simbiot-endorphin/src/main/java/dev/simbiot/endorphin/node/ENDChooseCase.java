package dev.simbiot.endorphin.node;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Program;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.statement.ExpressionStatement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDChooseCase extends BaseNode {
    @Nullable
    private final Program test;
    private final TemplateNode[] consequent;

    @JsonCreator
    public ENDChooseCase(@Nullable @JsonProperty("program") Program test,
                         @JsonProperty("consequent") TemplateNode[] consequent) {
        super("ENDChooseCase");
        this.test = test;
        this.consequent = consequent;
    }

    @Nullable
    public Expression getTest() {
        if (test != null) {
            return ((ExpressionStatement) test.getBody()[0]).getExpression();
        }

        return null;
    }

    public TemplateNode[] getConsequent() {
        return consequent;
    }
}
