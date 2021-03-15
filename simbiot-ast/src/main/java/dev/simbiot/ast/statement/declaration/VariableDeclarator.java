package dev.simbiot.ast.statement.declaration;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class VariableDeclarator extends BaseNode {
    private Identifier id;
    private Expression init;
    
    public VariableDeclarator(String id, Expression init) {
        this(new Identifier(id), init);
    }
    
    public VariableDeclarator(Identifier id, Expression init) {
        this();
        this.id = id;
        this.init = init;
    }
    
    public VariableDeclarator() {
        super("VariableDeclarator");
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    @Nullable
    public Expression getInit() {
        return init;
    }

    public void setInit(Expression init) {
        this.init = init;
    }
}
