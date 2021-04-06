package dev.simbiot.endorphin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.ConditionalExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.ObjectExpression;
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
    private static final Set<String> SELF_CLOSING_TAGS = new HashSet<>(Arrays.asList("area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"));
    private static long index = 0;
    private final String id;
    private final StringBuilder current;
    private final List<Statement> target;
    private final Map<String, String> urls;

    public EndorphinNodeVisitor(String id, List<Statement> target, Map<String, String> urls) {
        this.current = new StringBuilder();
        this.id = id;
        this.target = target;
        this.urls = urls;
    }

    private EndorphinNodeVisitor(EndorphinNodeVisitor parent, List<Statement> target) {
        this(parent.id, target, parent.urls);
    }

    public void accept(ENDTemplate template) {
        template.accept(this);
        flush();
    }

    @Override
    public void visit(ENDTemplate template) {
        writeElementStart(id);
        writeElementEnd(id, template.getBody());
    }

    @Override
    public void visit(ENDProgram program) {
        for (Statement statement : program.getBody()) {
            if (statement instanceof ExpressionStatement) {
                write(((ExpressionStatement) statement).getExpression(), true);
            }
        }
    }

    @Override
    public void visit(ENDLiteral literal) {
        final String data = literal.getString();

        if (!data.trim().isEmpty()) {
            write(data.replace("[\r\n\t]", ""));
        }
    }

    @Override
    public void visit(ENDInnerHTML innerHTML) {
        write(innerHTML.getExpression(), false);
    }

    @Override
    public void visit(ENDElement element) {
        writeElementStart(element.getName().getName());
        writeAttributes(element.getAttributes());
        writeElementEnd(element.getName().getName(), element.getBody());
    }

    @Override
    public void visit(ENDVariableStatement statement) {
        List<VariableDeclarator> declarators = new ArrayList<>();
        for (ENDVariable variable : statement.getVariables()) {
            declarators.add(new VariableDeclarator(variable.getName(), convert(variable.getValue())));
        }
        append(new VariableDeclaration(Kind.CONST, declarators));
    }

    @Override
    public void visit(ENDIfStatement statement) {
        append(new IfStatement(statement.getTest(), inner(statement.getConsequent())));
    }

    @Override
    public void visit(ENDChooseStatement statement) {
        final ENDChooseCase[] cases = statement.getCases();

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
    public void visit(ENDForEachStatement statement) {
        final String iteratorName = nextVarName();
        final Expression expression = ((ExpressionStatement) statement.getSelect().getBody()[0]).getExpression();

        append(new VariableDeclaration(iteratorName, new CallExpression("@iterator", expression)));
        append(new WhileStatement(
            new CallExpression(iteratorName, "hasNext"),
            new BlockStatement(
                new VariableDeclaration(statement.getValueName(), new CallExpression(iteratorName, "next")),
                inner(statement.getBody())
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
        write("<" + name);
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

                append(new IfStatement(
                    condition.getTest(),
                    new ExpressionStatement(condition.getConsequent()),
                    new ExpressionStatement(condition.getAlternate())
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

//    private Optional<String> getSlotName(Element element, String attrName) {
//        return Arrays.stream(element.getAttributes())
//            .filter(attribute -> attrName.equals(attribute.getName()))
//            .map(attribute -> {
//                final TemplateNode[] nodes = attribute.getValue();
//
//                if (nodes.length != 1 || !(nodes[0] instanceof Text)) {
//                    throw new IllegalArgumentException("name of slot expected to be a single text node (" + Arrays.toString(nodes) + " was provided)");
//                }
//
//                return (Text) nodes[0];
//            })
//            .map(Text::getData)
//            .findFirst();
//    }
}
