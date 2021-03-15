package dev.simbiot.ast.statement.declaration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ClassDeclaration.class, name = "ClassDeclaration"),
    @Type(value = FunctionDeclaration.class, name = "FunctionDeclaration"),
    @Type(value = VariableDeclaration.class, name = "VariableDeclaration"),
    @Type(value = VariableDeclarator.class, name = "VariableDeclarator")
})
public interface Declaration extends Statement {
}
