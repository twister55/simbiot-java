package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.pattern.Pattern;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class MemberExpression extends BaseNode implements Expression, Pattern {
    private final Expression object;
    private final Expression property;
    private final boolean computed;
    private final boolean optional;

    public MemberExpression(String objectId, int index) {
        this(new Identifier(objectId), new Literal(index), true, true);
    }

    public MemberExpression(String objectId, String propertyId) {
        this(new Identifier(objectId), new Identifier(propertyId), true, true);
    }

    public MemberExpression(Expression objectId, Expression propertyId) {
        this(objectId, propertyId, true, true);
    }

    @JsonCreator
    public MemberExpression(@JsonProperty("object") Expression object,
                            @JsonProperty("property") Expression property,
                            @JsonProperty("computed") boolean computed,
                            @JsonProperty("optional") boolean optional) {
        super("MemberExpression");
        this.object = object;
        this.property = property;
        this.computed = computed;
        this.optional = optional;
    }

    @Override
    public void accept(Expression.Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getObject() {
        return object;
    }

    public Expression getProperty() {
        return property;
    }

    public boolean isComputed() {
        return computed;
    }

    public boolean isOptional() {
        return optional;
    }

}
