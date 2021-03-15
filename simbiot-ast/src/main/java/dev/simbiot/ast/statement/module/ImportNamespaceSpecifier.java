package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ImportNamespaceSpecifier extends BaseModuleSpecifier {

    @JsonCreator
    public ImportNamespaceSpecifier(@JsonProperty("local") Identifier local) {
        super("ImportNamespaceSpecifier", local);
    }
}
