package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ImportDefaultSpecifier extends BaseModuleSpecifier {

    @JsonCreator
    public ImportDefaultSpecifier(@JsonProperty("local") Identifier local) {
        super("ImportDefaultSpecifier", local);
    }
}
