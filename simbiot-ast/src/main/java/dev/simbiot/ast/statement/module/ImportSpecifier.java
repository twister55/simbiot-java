package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ImportSpecifier extends BaseModuleSpecifier {
    private final Identifier imported;

    @JsonCreator
    public ImportSpecifier(@JsonProperty("local") Identifier local,
                           @JsonProperty("imported") Identifier imported) {
        super("ImportSpecifier", local);
        this.imported = imported;
    }

    public Identifier getImported() {
        return imported;
    }
}
