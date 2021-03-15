package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExportDefaultDeclaration extends BaseNode implements Statement, ModuleDeclaration {
    private final Expression declaration;

    @JsonCreator
    public ExportDefaultDeclaration(@JsonProperty("declaration") Expression declaration) {
        super("ExportDefaultDeclaration");
        this.declaration = declaration;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getDeclaration() {
        return declaration;
    }

}
