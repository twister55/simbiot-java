package dev.simbiot.endorphin.node;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Literal;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDLiteral extends Literal implements ENDNode, PlainStatement {

    @JsonCreator
    public ENDLiteral(@Nullable @JsonProperty("value") Object value,
                      @Nullable @JsonProperty("raw") String raw,
                      @Nullable @JsonProperty("regex") RegExp regex) {
        super(value, raw, regex);
    }

    @Override
    public void accept(ENDNode.Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Expression getExpression() {
        return this;
    }
}
