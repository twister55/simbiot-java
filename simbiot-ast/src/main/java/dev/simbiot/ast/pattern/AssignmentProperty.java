package dev.simbiot.ast.pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class AssignmentProperty extends BaseProperty<Pattern> {

    @JsonCreator
    public AssignmentProperty(@JsonProperty("key") Identifier key,
                              @JsonProperty("value") Pattern value) {
        super("AssignmentProperty", Kind.INIT, key, value, false, false, true);
    }
}
