package dev.simbiot.ast.statement.declaration;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class VariableDeclaration extends BaseNode implements Declaration {
    private final Kind kind;
    private final List<VariableDeclarator> declarations;

    public VariableDeclaration() {
        this(Kind.LET, new ArrayList<>());
    }

    @JsonCreator
    public VariableDeclaration(@JsonProperty("kind") Kind kind,
                               @JsonProperty("declarations") List<VariableDeclarator> declarations) {
        super("VariableDeclaration");
        this.kind = kind;
        this.declarations = declarations;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Kind getKind() {
        return kind;
    }

    public VariableDeclaration addDeclarator(Identifier id) {
        return addDeclarator(new VariableDeclarator(id));
    }

    public VariableDeclaration addDeclarator(Identifier id, Expression init) {
        return addDeclarator(new VariableDeclarator(id, init));
    }

    public VariableDeclaration addDeclarator(VariableDeclarator declarator) {
        declarations.add(declarator);
        return this;
    }

    public List<VariableDeclarator> getDeclarations() {
        return declarations;
    }

    public enum Kind {
        VAR,
        LET,
        CONST
    }
}
