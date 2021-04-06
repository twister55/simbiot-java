package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ENDAttributeValueExpression.class, name = "ENDAttributeValueExpression"),
    @Type(value = ENDLiteral.class, name = "Literal"),
    @Type(value = ENDProgram.class, name = "Program")
})
public interface PlainStatement extends Node {
}
