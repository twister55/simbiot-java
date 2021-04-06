package dev.simbiot.endorphin.node;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class AttributeStatement extends BaseNode {
    private final ENDAttribute[] attributes;
    private final ENDDirective[] directives;

    public AttributeStatement(ENDAttribute[] attributes, ENDDirective[] directives) {
        super("ENDAttributeStatement");
        this.attributes = attributes;
        this.directives = directives;
    }

    public ENDAttribute[] getAttributes() {
        return attributes;
    }

    public ENDDirective[] getDirectives() {
        return directives;
    }
}
