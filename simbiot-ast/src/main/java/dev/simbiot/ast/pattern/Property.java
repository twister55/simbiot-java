package dev.simbiot.ast.pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Property extends BaseProperty<Expression> {

    @JsonCreator
    public Property(@JsonProperty("kind") Kind kind,
                    @JsonProperty("kind") Identifier key,
                    @JsonProperty("kind") Expression value,
                    @JsonProperty("kind") boolean method,
                    @JsonProperty("kind") boolean shorthand,
                    @JsonProperty("kind") boolean computed) {
        super("Property", kind, key, value, method, shorthand, computed);
    }
}
