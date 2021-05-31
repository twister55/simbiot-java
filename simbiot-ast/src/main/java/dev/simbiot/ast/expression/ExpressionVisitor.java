package dev.simbiot.ast.expression;

import dev.simbiot.ast.expression.Expression.Visitor;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class ExpressionVisitor implements Visitor {

    protected abstract void visitDefault(Expression expression);

    @Override
    public void visit(ArrayExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(ArrowFunctionExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(AssignmentExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(AwaitExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(BinaryExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(CallExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(ClassExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(ConditionalExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(FunctionExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(Identifier expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(Literal expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(LogicalExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(MemberExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(MetaProperty expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(NewExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(ObjectExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(SequenceExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(Super expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(TaggedTemplateExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(TemplateLiteral expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(ThisExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(UnaryExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(UpdateExpression expression) {
        visitDefault(expression);
    }

    @Override
    public void visit(YieldExpression expression) {
        visitDefault(expression);
    }
}
