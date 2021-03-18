package dev.simbiot.ast.statement.declaration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class VariableDeclaration extends BaseNode implements Declaration {
    private final Kind kind;
    private final VariableDeclarator[] declarations;

    public VariableDeclaration(String id, Expression init) {
        this(new VariableDeclarator(id, init));
    }

    public VariableDeclaration(VariableDeclarator... declarations) {
        this(Kind.LET, declarations);
    }

    public VariableDeclaration(Kind kind, List<VariableDeclarator> declarations) {
        this(kind, declarations.toArray(new VariableDeclarator[0]));
    }

    @JsonCreator
    public VariableDeclaration(@JsonProperty("kind") Kind kind,
                               @JsonProperty("declarations") VariableDeclarator[] declarations) {
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

    public VariableDeclarator[] getDeclarations() {
        return declarations;
    }

    public enum Kind {
        VAR,
        LET,
        CONST
    }
}
