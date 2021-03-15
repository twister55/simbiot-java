package dev.simbiot.ast.statement.module;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.declaration.Declaration;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExportNamedDeclaration extends BaseNode implements Statement, ModuleDeclaration {
    private final List<ExportSpecifier> specifiers;
    private final Declaration declaration;
    private final Literal source;
    
    public ExportNamedDeclaration(Declaration declaration) {
        this(Collections.emptyList(), declaration, Literal.NULL);
    }
    
    @JsonCreator
    public ExportNamedDeclaration(@JsonProperty("specifiers") List<ExportSpecifier> specifiers,
                                  @JsonProperty("declaration") Declaration declaration,
                                  @JsonProperty("source") Literal source) {
        super("ExportNamedDeclaration");
        this.specifiers = specifiers;
        this.declaration = declaration;
        this.source = source;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public List<ExportSpecifier> getSpecifiers() {
        return specifiers;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public Literal getSource() {
        return source;
    }
}
