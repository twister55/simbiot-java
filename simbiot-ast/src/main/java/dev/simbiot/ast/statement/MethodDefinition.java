package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.FunctionExpression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class MethodDefinition extends BaseNode {
    private final Expression key;
    private final FunctionExpression value;
    private final Kind kind;
    private final boolean isComputed;
    private final boolean isStatic;

    public MethodDefinition(@JsonProperty("key") Expression key,
                            @JsonProperty("value") FunctionExpression value,
                            @JsonProperty("kind") Kind kind,
                            @JsonProperty("computed") boolean isComputed,
                            @JsonProperty("static") boolean isStatic) {
        super("MethodDefinition");
        this.key = key;
        this.value = value;
        this.kind = kind;
        this.isComputed = isComputed;
        this.isStatic = isStatic;
    }

    public Expression getKey() {
        return key;
    }

    public FunctionExpression getValue() {
        return value;
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isComputed() {
        return isComputed;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public enum Kind {
        CONSTRUCTOR,
        METHOD,
        GET,
        SET
    }
}
