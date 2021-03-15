package dev.simbiot.ast.statement.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ImportDeclaration extends BaseNode implements Statement, ModuleDeclaration {
    private final ModuleSpecifier[] specifiers;
    private final Literal source;

    @JsonCreator
    public ImportDeclaration(@JsonProperty("specifiers") ModuleSpecifier[] specifiers,
                             @JsonProperty("source") Literal source) {
        super("ImportDeclaration");
        this.specifiers = specifiers;
        this.source = source;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public ModuleSpecifier[] getSpecifiers() {
        return specifiers;
    }

    public Literal getSource() {
        return source;
    }
}
