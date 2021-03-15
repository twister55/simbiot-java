package dev.simbiot.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @JsonSubTypes.Type(value = Comment.Block.class, name = "Block"),
    @JsonSubTypes.Type(value = Comment.Line.class, name = "Line")
})
public abstract class Comment extends BaseNode {
    private final String value;

    protected Comment(String type, String value) {
        super(type);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class Block extends Comment {
        @JsonCreator
        public Block(@JsonProperty("value") String value) {
            super("Block", value);
        }
    }

    public static class Line extends Comment {
        @JsonCreator
        public Line(@JsonProperty("value") String value) {
            super("Line", value);
        }
    }
}
