package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDImport extends BaseNode implements ENDNode {
    private final String name;
    private final String href;

    @JsonCreator
    public ENDImport(@JsonProperty("name") String name,
                     @JsonProperty("href") String href) {
        super("ENDImport");
        this.name = name;
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public String getHref() {
        return href;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
