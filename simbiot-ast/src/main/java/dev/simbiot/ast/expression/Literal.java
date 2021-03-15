package dev.simbiot.ast.expression;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Literal extends BaseNode implements Expression {
    public static final Literal NULL = new Literal(null);
    
    private final Object value; // string | boolean | number;
    private final String raw;
    private final RegExp regex;

    public Literal(Object value) {
        this(value, null, null);
    }

    public Literal(@Nullable @JsonProperty("value") Object value,
                   @Nullable @JsonProperty("raw") String raw,
                   @Nullable @JsonProperty("regex") RegExp regex) {
        super("Literal");
        this.value = value;
        this.raw = raw;
        this.regex = regex;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    public boolean isNull() {
        return value == null;
    }

    public boolean isString() {
        return value instanceof String;
    }

    @Nullable
    public String getString() {
        return String.valueOf(value);
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public int getInt() {
        if (getString() != null) {
            return Integer.parseInt(getString());
        }
        return -1;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isTrue() {
        return Boolean.TRUE.equals(value);
    }

    @Nullable
    public RegExp getRegex() {
        return regex;
    }

    @Nullable
    public String getRaw() {
        return raw;
    }

    public static class RegExp {
        private final String pattern;
        private final String flags;

        @JsonCreator
        public RegExp(@JsonProperty("pattern") String pattern,
                      @JsonProperty("flags") String flags) {
            this.pattern = pattern;
            this.flags = flags;
        }

        public String getPattern() {
            return pattern;
        }

        public String getFlags() {
            return flags;
        }
    }
}
