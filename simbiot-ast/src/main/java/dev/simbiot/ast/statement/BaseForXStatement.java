package dev.simbiot.ast.statement;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class BaseForXStatement extends BaseNode implements Statement {
    private final VariableDeclaration left; // TODO Pattern;
    private final Expression right;
    private final Statement body;

    protected BaseForXStatement(String type, VariableDeclaration left, Expression right, Statement body) {
        super(type);
        this.left = left;
        this.right = right;
        this.body = body;
    }

    public VariableDeclaration getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Statement getBody() {
        return body;
    }
}
