package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Program;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Script extends BaseNode {
    private final Program content;
    private final Context context;

    @JsonCreator
    public Script(@JsonProperty("content") Program content,
                  @JsonProperty("context") Context context) {
         super("Script");
         this.content = content;
         this.context = context;
    }

    public Program getContent() {
        return content;
    }

    public Context getContext() {
        return context;
    }

    public enum Context {
        DEFAULT,
        MODULE
    }
}
