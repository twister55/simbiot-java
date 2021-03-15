package dev.simbiot.ast.statement.declaration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class VariableDeclaration extends BaseNode implements Declaration {
    private final Kind kind;
    private final VariableDeclarator[] declarations;

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
