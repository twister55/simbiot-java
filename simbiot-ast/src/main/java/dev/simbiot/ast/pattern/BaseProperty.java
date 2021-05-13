package dev.simbiot.ast.pattern;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Node;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class BaseProperty<V extends Node> extends BaseNode {
    private final Kind kind;
    private final Expression key;
    private final V value;
    private final boolean method;
    private final boolean shorthand;
    private final boolean computed;

    protected BaseProperty(String type, Kind kind, Expression key, V value, boolean method, boolean shorthand, boolean computed) {
        super(type);
        this.kind = kind;
        this.key = key;
        this.value = value;
        this.method = method;
        this.shorthand = shorthand;
        this.computed = computed;
    }

    public Kind getKind() {
        return kind;
    }

    public Expression getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public boolean isMethod() {
        return method;
    }

    public boolean isShorthand() {
        return shorthand;
    }

    public boolean isComputed() {
        return computed;
    }

    public enum Kind {
        INIT,
        GET,
        SET
    }
}
