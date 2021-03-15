package dev.simbiot.ast.pattern;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ArrayPattern.class, name = "ArrayPattern"),
    @Type(value = AssignmentPattern.class, name = "AssignmentPattern"),
    @Type(value = Identifier.class, name = "Identifier"),
    @Type(value = ObjectPattern.class, name = "ObjectPattern"),
    @Type(value = RestElement.class, name = "RestElement")
})
public interface Pattern extends Node {
}
