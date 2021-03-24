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
                    @JsonProperty("key") Identifier key,
                    @JsonProperty("value") Expression value,
                    @JsonProperty("method") boolean method,
                    @JsonProperty("shorthand") boolean shorthand,
                    @JsonProperty("computed") boolean computed) {
        super("Property", kind, key, value, method, shorthand, computed);
    }
}
