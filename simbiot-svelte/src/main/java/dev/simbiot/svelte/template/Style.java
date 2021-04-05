package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Style extends BaseNode {
    private final Content content;

    @JsonCreator
    public Style(@JsonProperty("content") Content content) {
        super("Style");
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    public String getHash() {
        String str = content.getStyles().replace("/\r/g", "");
        int hash = 5381;
        int i = str.length();
        while (i-- > 0) {
            hash = ((hash << 5) - hash) ^ str.charAt(i);
        }
        return Integer.toString(hash, 36);
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
