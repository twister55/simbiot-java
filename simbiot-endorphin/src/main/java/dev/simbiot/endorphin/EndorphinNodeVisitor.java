package dev.simbiot.endorphin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.ArrowFunctionExpression;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.ConditionalExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.IfStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.WhileStatement;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration.Kind;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.endorphin.node.ENDAttribute;
import dev.simbiot.endorphin.node.ENDAttributeValueExpression;
import dev.simbiot.endorphin.node.ENDChooseCase;
import dev.simbiot.endorphin.node.ENDChooseStatement;
import dev.simbiot.endorphin.node.ENDElement;
import dev.simbiot.endorphin.node.ENDForEachStatement;
import dev.simbiot.endorphin.node.ENDIfStatement;
import dev.simbiot.endorphin.node.ENDImport;
import dev.simbiot.endorphin.node.ENDInnerHTML;
import dev.simbiot.endorphin.node.ENDLiteral;
import dev.simbiot.endorphin.node.ENDProgram;
import dev.simbiot.endorphin.node.ENDTemplate;
import dev.simbiot.endorphin.node.ENDVariable;
import dev.simbiot.endorphin.node.ENDVariableStatement;
import dev.simbiot.endorphin.node.PlainStatement;
import dev.simbiot.endorphin.node.TemplateNode;
import dev.simbiot.endorphin.node.TemplateNode.Visitor;
import dev.simbiot.runtime.HTML;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinNodeVisitor implements Visitor {
    private static final String SLOT_DEFAULT_KEY = "__default__";
    private static final Set<String> SELF_CLOSING_TAGS = new HashSet<>(Arrays.asList("area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"));
    private static long index = 0;

    private final String id;
    private final String hash;
    private final StringBuilder current;
    private final List<Statement> target;
    private final Map<String, String> urls;

    public EndorphinNodeVisitor(String id, String hash, List<Statement> target) {
        this.id = id;
        this.hash = hash;
        this.current = new StringBuilder();
        this.target = target;
        this.urls = new HashMap<>();
    }

    private EndorphinNodeVisitor(EndorphinNodeVisitor parent, List<Statement> target) {
        this.id = parent.id;
        this.hash = parent.hash;
        this.current = new StringBuilder();
        this.target = target;
        this.urls = parent.urls;
    }

    public void accept(TemplateNode[] body) {
        for (TemplateNode node : body) {
            node.accept(this);
        }
        flush();
    }

    @Override
    public void visit(ENDImport node) {
        final String name = node.getName();
        final String href = node.getHref();
        final String componentId = href
            .substring(2, href.length() - 5)
            .replace("/", ".");

        urls.put(name, componentId);
    }

    @Override
    public void visit(ENDTemplate node) {
        write("<" + id + " " + hash + "-host");
        writeElementEnd(id, node.getBody());
    }

    @Override
    public void visit(ENDLiteral node) {
        final String data = node.getString();

        if (!data.trim().isEmpty()) {
            write(data.replace("[\r\n\t]", ""));
        }
    }

    @Override
    public void visit(ENDProgram node) {
        for (Statement statement : node.getBody()) {
            if (statement instanceof ExpressionStatement) {
                write(((ExpressionStatement) statement).getExpression(), true);
            }
        }
    }

    @Override
    public void visit(ENDInnerHTML node) {
        write(node.getExpression(), false);
    }

    @Override
    public void visit(ENDElement node) {
        final String name = node.getName().getName();

        if (node.isComponent()) {
            final List<Property> slots = new ArrayList<>();
            final List<TemplateNode> defaultSlotNodes = new ArrayList<>();

            for (TemplateNode child : node.getBody()) {
                if (child instanceof ENDElement) {
                    final Optional<String> slotId = getSlotName((ENDElement) child, "slot");

                    if (slotId.isPresent()) {
                        slots.add(new Property(slotId.get(), new ArrowFunctionExpression(inner(child))));
                        continue;
                    }
                }

                defaultSlotNodes.add(child);
            }

            if (!defaultSlotNodes.isEmpty()) {
                final Statement body = inner(defaultSlotNodes.toArray(new TemplateNode[0]));
                slots.add(new Property(new Identifier(SLOT_DEFAULT_KEY), new ArrowFunctionExpression(body)));
            }

            appendComponent(name, new ObjectExpression(Collections.emptyList()), new ObjectExpression(slots));
        } else if ("slot".equals(name)) {
            final Expression key = new Literal(getSlotName(node, "name").orElse(SLOT_DEFAULT_KEY));
            final Expression value = node.getBody().length > 0 ?
                new ArrowFunctionExpression(inner(node.getBody())) :
                new Identifier("@empty-slot");

            appendCall("@slot", key, value);
        } else {
            writeElementStart(name);
            writeAttributes(node.getAttributes());
            writeElementEnd(name, node.getBody());
        }
    }

    @Override
    public void visit(ENDVariableStatement node) {
        List<VariableDeclarator> declarators = new ArrayList<>();
        for (ENDVariable variable : node.getVariables()) {
            declarators.add(new VariableDeclarator(variable.getName(), convert(variable.getValue())));
        }
        append(new VariableDeclaration(Kind.CONST, declarators));
    }

    @Override
    public void visit(ENDIfStatement node) {
        append(new IfStatement(node.getTest(), inner(node.getConsequent())));
    }

    @Override
    public void visit(ENDChooseStatement node) {
        final ENDChooseCase[] cases = node.getCases();

        Statement result = null;
        for (int i = cases.length - 1; i >= 0 ; i--) {
            final ENDChooseCase block = cases[i];
            final Expression test = block.getTest();
            final Statement body = inner(block.getConsequent());

            result = test == null ? body : new IfStatement(test, body, result);
        }
        append(result);
    }

    @Override
    public void visit(ENDForEachStatement node) {
        final String iteratorName = nextVarName();
        final Expression expression = ((ExpressionStatement) node.getSelect().getBody()[0]).getExpression();

        append(new VariableDeclaration(iteratorName, new CallExpression("@iterator", expression)));
        append(new WhileStatement(
            new CallExpression(iteratorName, "hasNext"),
            new BlockStatement(
                new VariableDeclaration(node.getValueName(), new CallExpression(iteratorName, "next")),
                inner(node.getBody())
            )
        ));
    }

    private void flush() {
        if (current.length() > 0) {
            String value = current.toString();
            current.setLength(0);
            appendWrite(new Literal(value), false);
        }
    }

    private Statement inner(TemplateNode... children) {
        final List<Statement> result = new ArrayList<>();
        final EndorphinNodeVisitor visitor = new EndorphinNodeVisitor(this, result);
        for (TemplateNode node : children) {
            node.accept(visitor);
        }
        visitor.flush();
        return new BlockStatement(result);
    }

    private String nextVarName() {
        return "local" + (index++);
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

    private void appendComponent(String name, ObjectExpression properties, ObjectExpression slots) {
        appendCall("@component", new Literal(urls.get(name)), properties, slots);
    }

    private void appendCall(String callee, Expression... args) {
        append(new ExpressionStatement(new CallExpression(callee, args)));
    }

    private void append(Statement statement) {
        flush();
        target.add(statement);
    }

    private void writeElementStart(String name) {
        write("<" + name + " " + hash);
    }

    private void writeAttributes(ENDAttribute[] attributes) {
        for (ENDAttribute attribute : attributes) {
            write(" " + ((Identifier) attribute.getName()).getName() + "=\"");
            writeAttributeValue(attribute.getValue());
            write("\"");
        }
    }

    private void writeAttributeValue(PlainStatement value) {
        if (value instanceof ENDAttributeValueExpression) {
            final ENDAttributeValueExpression expression = (ENDAttributeValueExpression) value;

            for (PlainStatement element : expression.getElements()) {
                writeAttributeValue(element);
            }
        } else if (value instanceof Literal) {
            write(((Literal) value).getString());
        } else if (value instanceof Program) {
            final ExpressionStatement statement = (ExpressionStatement) ((Program) value).getBody()[0];
            final Expression expression = statement.getExpression();


            if (expression instanceof ConditionalExpression) {
                final ConditionalExpression condition = (ConditionalExpression) expression;
                final Identifier callee = new Identifier("@write");
                final Literal escape = new Literal(false);

                append(new IfStatement(
                    condition.getTest(),
                    new ExpressionStatement(new CallExpression(callee, condition.getConsequent(), escape)),
                    new ExpressionStatement(new CallExpression(callee, condition.getAlternate(), escape))
                ));
                return;
            }

            write(statement.getExpression(), true);
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

    private Expression convert(PlainStatement value) {
        if (value instanceof Literal) {
            return (Literal) value;
        } else if (value instanceof Program) {
            return ((ExpressionStatement) ((Program) value).getBody()[0]).getExpression();
        } else if (value instanceof ENDAttributeValueExpression) {
            List<Expression> arguments = new ArrayList<>();
            final ENDAttributeValueExpression expression = (ENDAttributeValueExpression) value;
            for (PlainStatement element : expression.getElements()) {
                arguments.add(convert(element));
            }
            return new CallExpression("@concat", arguments);
        }

        throw new UnsupportedNodeException(value, "PlainStatement to Expression converter");
    }

    private Optional<String> getSlotName(ENDElement element, String attrName) {
        return Arrays.stream(element.getAttributes())
            .filter(attribute -> attribute.getName() instanceof Identifier)
            .filter(attribute -> ((Identifier) attribute.getName()).getName().equals(attrName))
            .map(attribute -> {
                final PlainStatement value = attribute.getValue();
                if (value instanceof Literal) {
                    return ((Literal) value).getString();
                }
                throw new UnsupportedNodeException(value, "value of slot");
            })
            .findFirst();
    }
}
