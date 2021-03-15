package dev.simbiot.ast.statement.module;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class BaseModuleSpecifier extends BaseNode implements ModuleSpecifier {
    private final Identifier local;

    public BaseModuleSpecifier(String type, Identifier local) {
        super(type);
        this.local = local;
    }

    public Identifier getLocal() {
        return local;
    }
}
