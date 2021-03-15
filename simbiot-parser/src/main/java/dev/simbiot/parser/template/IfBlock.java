package dev.simbiot.parser.template;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class IfBlock extends Block implements TemplateNode {
    private final Expression expression;
    private final ElseBlock elseBlock;
    private final boolean elseIf;

    public IfBlock(@JsonProperty("expression") Expression expression,
                   @JsonProperty("children") TemplateNode[] children,
                   @JsonProperty("else") ElseBlock elseBlock,
                   @JsonProperty("elseif") boolean elseIf) {
        super("IfBlock", children);
        this.expression = expression;
        this.elseBlock = elseBlock;
        this.elseIf = elseIf;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean hasElse() {
        return this.elseBlock != null;
    }

    @Nullable
    public ElseBlock getElse() {
        return elseBlock;
    }

    public boolean isElseIf() {
        return elseIf;
    }
}
