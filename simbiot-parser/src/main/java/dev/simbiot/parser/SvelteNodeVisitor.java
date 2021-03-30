package dev.simbiot.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.expression.ObjectExpression;
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
import dev.simbiot.runtime.HTML;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SvelteNodeVisitor implements Visitor {
    private static final Set<String> SELF_CLOSING_TAGS = new HashSet<>(Arrays.asList("area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"));
    private static long index = 0;
    private final StringBuilder current;
    private final List<Statement> target;
    private final Map<String, String> urls;
    @Nullable
    private final String hash;

    public SvelteNodeVisitor(List<Statement> target, Map<String, String> urls, @Nullable String hash) {
        this.current = new StringBuilder();
        this.target = target;
        this.urls = urls;
        this.hash = hash;
    }

    private SvelteNodeVisitor(SvelteNodeVisitor parent, List<Statement> target) {
        this(target, parent.urls, parent.hash);
    }

    public void accept(Fragment fragment) {
        fragment.accept(this);
        flush();
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
        final String iteratorName = nextVarName();
        final String indexName = block.getIndex();

        if (indexName != null) {
            append(new VariableDeclaration(indexName, new Literal(0)));
        }
        append(new VariableDeclaration(iteratorName, new CallExpression("@iterator", block.getExpression())));
        append(loop(block.getContext(), block.getChildren(), iteratorName, indexName));
    }

    @Override
    public void visit(Element element) {
        writeElementStart(element.getName());
        writeAttributes(element.getAttributes());
        writeElementEnd(element.getName(), element.getChildren());
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
        appendComponent(component.getName(), component.getProperties());
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
        final String data = text.getData();

        if (!data.trim().isEmpty()) {
            write(data.replace("[\r\n\t]", ""));
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
        final SvelteNodeVisitor visitor = new SvelteNodeVisitor(this, result);
        visitor.accept(new Fragment(children));
        return new BlockStatement(result);
    }

    private String nextVarName() {
        return "local" + (index++);
    }

    private Statement loop(Pattern context, TemplateNode[] body, String iteratorName, @Nullable String indexName) {
        final List<VariableDeclarator> vars = new ArrayList<>();
        final CallExpression callNext = new CallExpression(iteratorName, "next");

        if (context instanceof Identifier) {
            vars.add(new VariableDeclarator((Identifier) context, callNext));
        } else if (context instanceof ObjectPattern) {
            final String contextName = nextVarName();

            vars.add(new VariableDeclarator(contextName, callNext));
            for (Property prop : ((ObjectPattern) context).getProperties()) {
                vars.add(new VariableDeclarator(
                    prop.getKey(), new MemberExpression(contextName, ((Identifier) prop.getValue()).getName())
                ));
            }
        } else {
            throw new UnsupportedNodeException(context, "context of EachBlock");
        }

        Statement incrementIndex = Statement.EMPTY;
        if (indexName != null) {
            incrementIndex = new ExpressionStatement(
                new UpdateExpression(Operator.INCREMENT, new Identifier(indexName), false)
            );
        }

        return new WhileStatement(
            new CallExpression(iteratorName, "hasNext"),
            new BlockStatement(
                new VariableDeclaration(Kind.LET, vars),
                incrementIndex,
                inner(body)
            )
        );
    }

    private void write(String value) {
        current.append(value);
    }

    private void write(Expression expression, boolean escape) {
        if (expression instanceof Literal) {
            String value = ((Literal) expression).getString();
            if (value != null && !value.isEmpty()) {
                write(escape ? HTML.escape(value) : value);
            }
            return;
        }

        appendWrite(expression, escape);
    }

    private void appendWrite(Expression expression, boolean escape) {
        appendCall("@write", expression, new Literal(escape));
    }

    private void appendComponent(String name, ObjectExpression properties) {
        appendCall("@component", new Literal(urls.get(name)), properties);
    }

    private void appendCall(String callee, Expression... args) {
        append(new ExpressionStatement(new CallExpression(callee, args)));
    }

    private void append(Statement statement) {
        flush();
        target.add(statement);
    }

    private void writeElementStart(String name) {
        write("<" + name);
    }

    private void writeAttributes(Attribute[] attributes) {
        boolean hasClassAttr = false;
        for (Attribute attribute : attributes) {
            if (attribute instanceof EventHandler) {
                continue;
            }

            if ("class".equals(attribute.getName())) {
                hasClassAttr = true;
            }

            write(" " + attribute.getName()  + "=\"");
            for (TemplateNode v : attribute.getValue()) {
                v.accept(this);
            }
            write("\"");
        }

        if (!hasClassAttr && hash != null) {
            write(" class=\"svelte-" + hash + "\"");
        }
    }

    private void writeElementEnd(String name, TemplateNode[] children) {
        if (children.length == 0 && isSelfClosingTag(name)) {
            write("/>");
        } else {
            write(">");
            for (TemplateNode child : children) {
                child.accept(this);
            }
            write("</" + name + ">");
        }
    }

    private boolean isSelfClosingTag(String name) {
        return SELF_CLOSING_TAGS.contains(name) || name.toLowerCase().equals("!doctype");
    }
}
