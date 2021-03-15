package dev.simbiot.ast;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SourceLocation {
    @Nullable
    private final String source;
    private final Position start;
    private final Position end;

    @JsonCreator
    public SourceLocation(@Nullable @JsonProperty("source") String source,
                          @JsonProperty("start") Position start,
                          @JsonProperty("end") Position end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Nullable
    public String getSource() {
        return source;
    }

    @Nullable
    public Position getStart() {
        return start;
    }

    @Nullable
    public Position getEnd() {
        return end;
    }

    public static class Position {
        /** >= 1 */
        public final int line;
        /** >= 0 */
        public final int column;

        @JsonCreator
        public Position(@JsonProperty("line") int line,
                        @JsonProperty("column") int column) {
            this.line = line;
            this.column = column;
        }
    }
}
