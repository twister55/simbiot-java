package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TemplateElement extends BaseNode {
    private final Value value;
    private final boolean tail;

    protected TemplateElement(@JsonProperty("value") Value value,
                              @JsonProperty("tail") boolean tail) {
        super("TemplateElement");
        this.value = value;
        this.tail = tail;
    }

    public Value getValue() {
        return value;
    }

    public boolean isTail() {
        return tail;
    }

    public static class Value {
        private final String raw;
        private final String cooked;

        @JsonCreator
        public Value(@JsonProperty("key") String raw,
                     @JsonProperty("cooked") String cooked) {
            this.raw = raw;
            this.cooked = cooked;
        }

        public String getRaw() {
            return raw;
        }

        public String getCooked() {
            return cooked;
        }
    }
}
