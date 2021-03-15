package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ExportSpecifier.class, name = "ExportSpecifier"),
    @Type(value = ImportDefaultSpecifier.class, name = "ImportDefaultSpecifier"),
    @Type(value = ImportNamespaceSpecifier.class, name = "ImportNamespaceSpecifier"),
    @Type(value = ImportSpecifier.class, name = "ImportSpecifier")
})
public interface ModuleSpecifier extends Node {
}
