package dev.simbiot.svelte.template;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class Block extends BaseNode implements TemplateNode {
    private final TemplateNode[] children;

    protected Block(String type, TemplateNode[] children) {
        super(type);
        this.children = children;
    }

    @Override
    public void accept(Visitor visitor) {
        for (TemplateNode child : children) {
            child.accept(visitor);
        }
    }

    public boolean hasChildren() {
        return children.length > 0;
    }

    public TemplateNode[] getChildren() {
        return children;
    }
}
