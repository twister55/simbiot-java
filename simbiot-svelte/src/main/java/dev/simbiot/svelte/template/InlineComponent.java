package dev.simbiot.svelte.template;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.pattern.Property;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class InlineComponent extends Element {

    @JsonCreator
    public InlineComponent(@JsonProperty("name") String name,
                           @JsonProperty("children") TemplateNode[] children,
                           @JsonProperty("attributes") Attribute[] attributes) {
        super("InlineComponent", name, children, attributes);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public ObjectExpression getProperties() {
        List<Property> properties = new ArrayList<>();
        for (Attribute attribute : getAttributes()) {
            properties.add(new Property(attribute.getName(), convert(attribute.getValue())));
        }
        return new ObjectExpression(properties);
    }

    private Expression convert(TemplateNode[] nodes) {
        List<Expression> result = new ArrayList<>();
        for (TemplateNode node : nodes) {
            result.add(convert(node));
        }
        return collapse(result);
    }

    private Expression collapse(List<Expression> expressions) {
        if (expressions.size() == 1) {
            return expressions.get(0);
        }

        return new CallExpression("@concat", expressions);
    }

    private Expression convert(TemplateNode node) {
        if (node instanceof Text) {
            return new Literal(((Text) node).getData());
        }

        if (node instanceof MustacheTag) {
            return ((MustacheTag) node).getExpression();
        }

        throw new UnsupportedNodeException(node, "component property");
    }
}
