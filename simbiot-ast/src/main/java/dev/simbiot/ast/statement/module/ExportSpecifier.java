package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExportSpecifier extends BaseModuleSpecifier {
    private final Identifier exported;

    @JsonCreator
    public ExportSpecifier(@JsonProperty("local") Identifier local,
                           @JsonProperty("exported") Identifier exported) {
        super("ExportSpecifier", local);
        this.exported = exported;
    }

    public Identifier getExported() {
        return exported;
    }
}
