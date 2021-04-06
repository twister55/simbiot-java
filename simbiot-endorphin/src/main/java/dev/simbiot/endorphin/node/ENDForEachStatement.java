package dev.simbiot.endorphin.node;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Program;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDForEachStatement extends BaseNode implements TemplateNode {
    private final TemplateNode[] body;
    private final Program select;
    @Nullable
    private final Program key;
    /** Name of local variable for referencing iterator index */
    private final String indexName;
    /** Name of local variable for referencing iterator key */
    private final String keyName;
    /** Name of local variable for referencing iterator value */
    private final String valueName;

    public ENDForEachStatement(@JsonProperty("body") TemplateNode[] body,
                               @JsonProperty("select") Program select,
                               @Nullable @JsonProperty("key") Program key,
                               @JsonProperty("indexName") String indexName,
                               @JsonProperty("keyName") String keyName,
                               @JsonProperty("valueName") String valueName) {
        super("ENDForEachStatement");
        this.body = body;
        this.select = select;
        this.key = key;
        this.indexName = indexName;
        this.keyName = keyName;
        this.valueName = valueName;
    }

    public TemplateNode[] getBody() {
        return body;
    }

    public Program getSelect() {
        return select;
    }

    public Program getKey() {
        return key;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getValueName() {
        return valueName;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
