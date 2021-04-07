package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;
import dev.simbiot.endorphin.node.expression.IdentifierWithContext;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = IdentifierWithContext.class, name = "Identifier"),
    @Type(value = ENDProgram.class, name = "Program")
})
public interface AttributeName extends Node {
}
