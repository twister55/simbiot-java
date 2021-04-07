package dev.simbiot.endorphin.node.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.endorphin.node.AttributeName;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class IdentifierWithContext extends Identifier implements AttributeName {
    private final Context context;

    @JsonCreator
    public IdentifierWithContext(@JsonProperty("name") String name,
                                 @JsonProperty("context") Context context) {
        super(name);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public enum Context {
        PROPERTY("property"),
        STATE("state"),
        VARIABLE("variable"),
        STORE("store"),
        STORE_HOST("store-host"),
        HELPER("helper"),
        DEFINITION("definition"),
        ARGUMENT("argument");

        private final String value;

        Context(String value) {
            this.value = value;
        }

        @JsonValue
        public String value() {
            return value;
        }

        public static Context of(String name) {
            return valueOf(name.toUpperCase().replace("-", "_"));
        }
    }
}
