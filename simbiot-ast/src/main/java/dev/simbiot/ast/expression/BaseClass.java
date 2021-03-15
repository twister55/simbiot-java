package dev.simbiot.ast.expression;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.statement.ClassBody;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class BaseClass extends BaseNode {
    private final Expression superClass;
    private final ClassBody body;

    protected BaseClass(String type, Expression superClass, ClassBody body) {
        super(type);
        this.superClass = superClass;
        this.body = body;
    }

    @Nullable
    public Expression getSuperClass() {
        return superClass;
    }

    public ClassBody getBody() {
        return body;
    }
}
