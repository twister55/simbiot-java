package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDVariableStatement extends BaseNode implements TemplateNode {
    private final ENDVariable[] variables;

    @JsonCreator
    public ENDVariableStatement(@JsonProperty("variables") ENDVariable[] variables) {
        super("ENDVariableStatement");
        this.variables = variables;
    }

    public ENDVariable[] getVariables() {
        return variables;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
