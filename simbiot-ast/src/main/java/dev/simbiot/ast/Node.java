package dev.simbiot.ast;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = { @Type(value = Expression.class), @Type(value = Statement.class), @Type(value = Pattern.class) })
public interface Node {

    String getType();
}
