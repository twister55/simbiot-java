package dev.simbiot.parser;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.expression.UpdateExpression;
import dev.simbiot.ast.expression.UpdateExpression.Operator;
import dev.simbiot.ast.pattern.ObjectPattern;
import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.IfStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.WhileStatement;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration.Kind;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
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
        append(new ExpressionStatement(new CallExpression("debug", tag.getIdentifiers())));
    }

    @Override
    public void visit(EachBlock block) {
        final VariableDeclarator iterator = new VariableDeclarator(
            "__iterator__", new CallExpression("iterator", block.getExpression())
        );
        final CallExpression hasNext = new CallExpression(
            new MemberExpression("__iterator__", "hasNext")
        );

        if (block.getIndex() != null) {
            append(new VariableDeclaration(new VariableDeclarator(block.getIndex(), new Literal(0))));
        }
        append(new VariableDeclaration(iterator));
        append(new WhileStatement(hasNext, getBody(block)));
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
        IfStatement statement = new IfStatement(
            block.getExpression(),
            inner(block.getChildren()),
            block.getElse() != null ? inner(block.getElse().getChildren()) : null
        );

        append(statement);
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
            appendWrite(new Literal(value), false);
        }
    }

    private Statement inner(TemplateNode[] children) {
        final List<Statement> result = new ArrayList<>();
        accept(new Fragment(children), result);
        return new BlockStatement(result);
    }

    private Statement getBody(EachBlock block) {
        final Pattern context = block.getContext();
        final List<VariableDeclarator> vars = new ArrayList<>();

        if (context instanceof Identifier) {
            vars.add(new VariableDeclarator(
                (Identifier) context, new CallExpression(new MemberExpression("__iterator__", "next")))
            );
        } else if (context instanceof ObjectPattern) {
            vars.add(new VariableDeclarator(
                "context", new CallExpression(new MemberExpression("iterator", "next"))
            ));

            ObjectPattern objectPattern = (ObjectPattern) context;
            for (Property prop : objectPattern.getProperties()) {
                vars.add(new VariableDeclarator(
                    prop.getKey(), new MemberExpression("context", ((Identifier) prop.getValue()).getName())
                ));
            }
        } else {
            throw new ParseException(context.getType() + " is not supported in context of EachBlock");
        }

        Statement incrementIndex = Statement.EMPTY;
        if (block.getIndex() != null) {
            incrementIndex = new ExpressionStatement(
                new UpdateExpression(Operator.INCREMENT, new Identifier(block.getIndex()), false)
            );
        }

        return new BlockStatement(new Statement[] {
            new VariableDeclaration(Kind.LET, vars),
            incrementIndex,
            inner(block.getChildren())
        });
    }

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

        appendWrite(expression, escape);
    }

    private void appendWrite(Expression expression, boolean escape) {
        append(new ExpressionStatement(new CallExpression("write", expression, new Literal(escape))));
    }

    private void append(Statement statement) {
        flush();
        target.add(statement);
    }
}
