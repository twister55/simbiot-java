package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Node;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Style extends BaseNode {
    private final Content content;
    private final Attribute[] attributes;
    private final Node[] children;

    @JsonCreator
    public Style(@JsonProperty("content") Content content,
                 @JsonProperty("attributes") Attribute[] attributes,
                 @JsonProperty("children") Node[] children) {
        super("Style");
        this.content = content;
        this.attributes = attributes;
        this.children = children;
    }

    public Content getContent() {
        return content;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public Node[] getChildren() {
        return children;
    }

    public static class Content {
        private final String styles;
        private final int start;
        private final int end;

        @JsonCreator
        public Content(@JsonProperty("styles") String styles,
                       @JsonProperty("start") int start,
                       @JsonProperty("end") int end) {
            this.styles = styles;
            this.start = start;
            this.end = end;
        }

        public String getStyles() {
            return styles;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
