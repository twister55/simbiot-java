package dev.simbiot.compiler;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.expression.UnaryExpression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class BuiltIn {
    public static final String CONSTANTS_FIELD_NAME = "CONSTANTS";
    public static final String COMPONENTS_FIELD_NAME = "components";

    public static final Identifier WRITE = new Identifier("@write");
    public static final Identifier WRITER = new Identifier("@writer");
    public static final MemberExpression WRITER_WRITE = new MemberExpression(WRITER, new Identifier("write"));

    public static final Identifier PROPS = new Identifier("@props");
    public static final MemberExpression PROPS_GET = new MemberExpression(PROPS, new Identifier("get"));
    public static final MemberExpression PROPS_GET_OR_DEFAULT = new MemberExpression(PROPS, new Identifier("getOrDefault"));

    public static final Identifier ARG = new Identifier("@arg0");
    public static final MemberExpression ARG_GET_OR_DEFAULT = new MemberExpression(ARG, new Identifier("getOrDefault"));

    public static Expression toBoolean(Expression expression) {
        return new UnaryExpression("!", new UnaryExpression("!", expression));
    }
}
