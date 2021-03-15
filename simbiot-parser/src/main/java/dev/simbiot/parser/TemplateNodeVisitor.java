package dev.simbiot.parser;

import java.util.List;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.parser.template.Attribute;
import dev.simbiot.parser.template.AwaitBlock;
import dev.simbiot.parser.template.Comment;
import dev.simbiot.parser.template.DebugTag;
import dev.simbiot.parser.template.EachBlock;
import dev.simbiot.parser.template.Element;
import dev.simbiot.parser.template.EventHandler;
import dev.simbiot.parser.template.Fragment;
import dev.simbiot.parser.template.Head;
import dev.simbiot.parser.template.IfBlock;
import dev.simbiot.parser.template.InlineComponent;
import dev.simbiot.parser.template.KeyBlock;
import dev.simbiot.parser.template.MustacheTag;
import dev.simbiot.parser.template.RawMustacheTag;
import dev.simbiot.parser.template.Slot;
import dev.simbiot.parser.template.TemplateNode;
import dev.simbiot.parser.template.TemplateNode.Visitor;
import dev.simbiot.parser.template.Text;
import dev.simbiot.parser.template.Title;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TemplateNodeVisitor implements Visitor {
    private final List<Statement> target;
    private final StringBuilder current;

    public static void accept(Fragment fragment, List<Statement> target) {
        final TemplateNodeVisitor visitor = new TemplateNodeVisitor(target);
        fragment.accept(visitor);
        visitor.flush();
    }

    private TemplateNodeVisitor(List<Statement> target) {
        this.target = target;
        this.current = new StringBuilder();
    }

    @Override
    public void visit(AwaitBlock block) {

    }

    @Override
    public void visit(Comment comment) {
        write("<!--" + comment.getData() + "-->");
    }

    @Override
    public void visit(DebugTag tag) {
        add(new ExpressionStatement(new CallExpression("debug", tag.getIdentifiers())));
    }

    @Override
    public void visit(EachBlock block) {
//        Identifier index = new Identifier(block.getIndex() == null ? "___index___" : block.getIndex());
//        VariableDeclaration init = new VariableDeclaration(VariableDeclaration.Kind.LET)
//                .addDeclarator(index, new Literal(0));
//
//        Pattern context = block.getContext();
//        if (context instanceof Identifier) {
//            init.addDeclarator((Identifier) context);
//        } else if (context instanceof ObjectPattern) {
//            ObjectPattern objectPattern = (ObjectPattern) context;
//            objectPattern.getProperties().forEach(property -> init.addDeclarator(property.getKey()));
//        } else {
//            throw new ParseException(context.getType() + " is not supported in context of EachBlock");
//        }
//
//        List<Expression> update = new ArrayList<>();
//        update.add(UpdateExpression.increment(index));

//        if (context instanceof Identifier) {
//            update.add(AssignmentExpression.assign(context, build("read", block.getExpression(), index)));
//        } else {
//            ObjectPattern objectPattern = (ObjectPattern) context;
//            objectPattern.getProperties().forEach(property -> {
//                CallExpression value = build("get", block.getExpression(), index);
//                AssignmentExpression assign = AssignmentExpression.assign(
//                        property.getKey(),
//                        build("access", value, property.getValue())
//                );
//
//                update.add(assign);
//            });
//        }

//        ForStatement statement = new ForStatement();
//        statement.setInit(init);
////        statement.setTest(BinaryExpression.less(index, build("size", block.getExpression())));
//        statement.setUpdate(new SequenceExpression(update));
//        statement.setBody(inner(block.getChildren()));
//
//        add(statement);
    }

    @Override
    public void visit(Element element) {
        write("<" + element.getName());

        for (Attribute attribute : element.getAttributes()) {
            if (attribute instanceof EventHandler) {
                continue;
            }

            write(" " + attribute.getName()  + "=\"");
            for (TemplateNode v : attribute.getValue()) {
                v.accept(this);
            }
            write("\"");
        }

        write(">"); // TODO check if self closing
        for (TemplateNode child : element.getChildren()) {
            child.accept(this);
        }
        write("</" + element.getName() + ">");
    }

    @Override
    public void visit(Head head) {

    }

    @Override
    public void visit(IfBlock block) {
//        IfStatement statement = new IfStatement();
//        statement.setTest(block.getExpression());
//        statement.setConsequent(inner(block.getChildren()));
//        if (block.getElse() != null) {
//            statement.setAlternate(inner(block.getElse().getChildren()));
//        }
//
//        add(statement);
    }

    @Override
    public void visit(InlineComponent component) {

    }

    @Override
    public void visit(KeyBlock block) {

    }

    @Override
    public void visit(MustacheTag tag) {
        write(tag.getExpression(), true);
    }

    @Override
    public void visit(RawMustacheTag tag) {
        write(tag.getExpression(), false);
    }

    @Override
    public void visit(Slot slot) {

    }

    @Override
    public void visit(Text text) {
        if (!text.getData().trim().isEmpty()) {
            final String value = text.getData()
                    .replace("\r", "")
                    .replace("\n", "")
                    .replace("\t", "");

            write(value);
        }
    }

    @Override
    public void visit(Title title) {

    }

    public void flush() {
        if (current.length() > 0) {
            String value = current.toString();
            current.setLength(0);
            addWriteStatement(new Literal(value), false);
        }
    }

//    private Statement inner(TemplateNode[] children) {
//        final ArrayList<Statement> result = new ArrayList<>();
//        accept(new Fragment(children), result);
//        return wrap(result);
//    }

    private void write(String value) {
        current.append(value);
    }

    private void write(Expression expression, boolean escape) {
        if (expression instanceof Literal) {
            String value = ((Literal) expression).getString();
            if (value != null && !value.isEmpty()) {
                write(escape ? Runtime.escape(value) : value);
            }
            return;
        }

        addWriteStatement(expression, escape);
    }

    private void addWriteStatement(Expression expression, boolean escape) {
        add(new ExpressionStatement(new CallExpression("write", expression, new Literal(escape))));
    }

    private void add(Statement statement) {
        flush();
        target.add(statement);
    }
}
