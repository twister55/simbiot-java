package dev.simbiot.ast.expression;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class BaseCallExpression extends BaseNode implements Expression {
    private final Expression callee;
    private final Expression[] arguments; // TODO SpreadElement
    private final boolean optional;

    protected BaseCallExpression(String type, Expression callee, Expression[] arguments, boolean optional) {
        super(type);
        this.callee = callee;
        this.arguments = arguments;
        this.optional = optional;
    }

    public Expression getCallee() {
        return callee;
    }

    public Expression getArgument(int index) {
        return arguments[index];
    }
    
    public Expression[] getArguments() {
        return arguments;
    }

    public boolean isOptional() {
        return optional;
    }

}
