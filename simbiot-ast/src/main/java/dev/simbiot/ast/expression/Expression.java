package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ArrayExpression.class, name = "ArrayExpression"),
    @Type(value = ArrowFunctionExpression.class, name = "ArrowFunctionExpression"),
    @Type(value = AssignmentExpression.class, name = "AssignmentExpression"),
    @Type(value = AwaitExpression.class, name = "AwaitExpression"),
    @Type(value = BinaryExpression.class, name = "BinaryExpression"),
    @Type(value = CallExpression.class, name = "CallExpression"),
    @Type(value = ClassExpression.class, name = "ClassExpression"),
    @Type(value = ConditionalExpression.class, name = "ConditionalExpression"),
    @Type(value = FunctionExpression.class, name = "FunctionExpression"),
    @Type(value = Identifier.class, name = "Identifier"),
    @Type(value = Literal.class, name = "Literal"),
    @Type(value = LogicalExpression.class, name = "LogicalExpression"),
    @Type(value = MemberExpression.class, name = "MemberExpression"),
    @Type(value = MetaProperty.class, name = "MetaProperty"),
    @Type(value = NewExpression.class, name = "NewExpression"),
    @Type(value = ObjectExpression.class, name = "ObjectExpression"),
    @Type(value = SequenceExpression.class, name = "SequenceExpression"),
    @Type(value = Super.class, name = "Super"),
    @Type(value = TaggedTemplateExpression.class, name = "TaggedTemplateExpression"),
    @Type(value = TemplateLiteral.class, name = "TemplateLiteral"),
    @Type(value = ThisExpression.class, name = "ThisExpression"),
    @Type(value = UnaryExpression.class, name = "UnaryExpression"),
    @Type(value = UpdateExpression.class, name = "UpdateExpression"),
    @Type(value = YieldExpression.class, name = "YieldExpression")
})
public interface Expression extends Node {

    void accept(Visitor visitor);

    interface Visitor {

        void visit(ArrayExpression expression);

        void visit(ArrowFunctionExpression expression);

        void visit(AssignmentExpression expression);

        void visit(AwaitExpression expression);

        void visit(BinaryExpression expression);

        void visit(CallExpression expression);

        void visit(ClassExpression expression);

        void visit(ConditionalExpression expression);

        void visit(FunctionExpression expression);

        void visit(Identifier expression);

        void visit(Literal expression);

        void visit(LogicalExpression expression);

        void visit(MemberExpression expression);

        void visit(MetaProperty expression);

        void visit(NewExpression expression);

        void visit(ObjectExpression expression);

        void visit(SequenceExpression expression);

        void visit(Super expression);

        void visit(TaggedTemplateExpression expression);

        void visit(TemplateLiteral expression);

        void visit(ThisExpression expression);

        void visit(UnaryExpression expression);

        void visit(UpdateExpression expression);

        void visit(YieldExpression expression);
    }
}
