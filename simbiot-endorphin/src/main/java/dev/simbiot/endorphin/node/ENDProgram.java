package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.SourceType;
import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDProgram extends Program implements TemplateNode, AttributeName, PlainStatement {

    @JsonCreator
    public ENDProgram(@JsonProperty("sourceType") SourceType sourceType,
                      @JsonProperty("body") Statement... body) {
        super(sourceType, body);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
