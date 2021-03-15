package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.pattern.Pattern;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CatchClause extends BaseNode {
    private final Pattern param;
    private final BlockStatement body;

    @JsonCreator
    public CatchClause(@JsonProperty("param") Pattern param,
                       @JsonProperty("body") BlockStatement body) {
        super("CatchClause");
        this.param = param;
        this.body = body;
    }

    public Pattern getParam() {
        return param;
    }

    public BlockStatement getBody() {
        return body;
    }
}
