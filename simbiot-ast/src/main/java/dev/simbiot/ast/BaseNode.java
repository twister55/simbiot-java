package dev.simbiot.ast;

import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class BaseNode implements Node {
    private final String type;

    private int start;
    private int end;
    private SourceLocation loc;

    private Comment[] leadingComments;
    private Comment[] trailingComments;

    protected BaseNode(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public SourceLocation getLoc() {
        return loc;
    }

    public void setLoc(SourceLocation loc) {
        this.loc = loc;
    }

    @Nullable
    public Comment[] getLeadingComments() {
        return leadingComments;
    }

    public void setLeadingComments(Comment[] leadingComments) {
        this.leadingComments = leadingComments;
    }

    @Nullable
    public Comment[] getTrailingComments() {
        return trailingComments;
    }

    public void setTrailingComments(Comment[] trailingComments) {
        this.trailingComments = trailingComments;
    }
}
