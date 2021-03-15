package dev.simbiot.ast.expression;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.pattern.Pattern;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class BaseFunction extends BaseNode {
    private final Pattern[] params;
    private final boolean generator;
    private final boolean async;

    protected BaseFunction(String type, Pattern[] params, boolean generator, boolean async) {
        super(type);
        this.params = params;
        this.generator = generator;
        this.async = async;
    }

    public Pattern[] getParams() {
        return params;
    }

    public boolean isGenerator() {
        return generator;
    }

    public boolean isAsync() {
        return async;
    }
}
