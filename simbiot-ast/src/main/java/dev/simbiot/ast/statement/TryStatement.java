package dev.simbiot.ast.statement;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TryStatement extends BaseNode implements Statement {
    private final BlockStatement block;
    private final CatchClause handler;
    private final BlockStatement finalizer;

    @JsonCreator
    public TryStatement(@JsonProperty("block") BlockStatement block,
                        @JsonProperty("handler") CatchClause handler,
                        @JsonProperty("finalizer") BlockStatement finalizer) {
        super("TryStatement");
        this.block = block;
        this.handler = handler;
        this.finalizer = finalizer;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public BlockStatement getBlock() {
        return block;
    }

    @Nullable
    public CatchClause getHandler() {
        return handler;
    }

    @Nullable
    public BlockStatement getFinalizer() {
        return finalizer;
    }
}
