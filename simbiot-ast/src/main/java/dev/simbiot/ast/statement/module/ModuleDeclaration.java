package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ExportAllDeclaration.class, name = "ExportAllDeclaration"),
    @Type(value = ExportDefaultDeclaration.class, name = "ExportDefaultDeclaration"),
    @Type(value = ExportNamedDeclaration.class, name = "ExportNamedDeclaration"),
    @Type(value = ImportDeclaration.class, name = "ImportDeclaration")
})
public interface ModuleDeclaration extends Statement {
}
