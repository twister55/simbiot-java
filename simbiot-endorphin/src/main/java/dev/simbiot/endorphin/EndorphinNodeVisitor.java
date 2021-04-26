package dev.simbiot.endorphin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.ArrowFunctionExpression;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.expression.UpdateExpression;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.IfStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.WhileStatement;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration.Kind;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.compiler.ProgramBuilder;
import dev.simbiot.endorphin.node.ENDAttribute;
import dev.simbiot.endorphin.node.ENDChooseCase;
import dev.simbiot.endorphin.node.ENDChooseStatement;
import dev.simbiot.endorphin.node.ENDElement;
import dev.simbiot.endorphin.node.ENDForEachStatement;
import dev.simbiot.endorphin.node.ENDIfStatement;
import dev.simbiot.endorphin.node.ENDImport;
import dev.simbiot.endorphin.node.ENDInnerHTML;
import dev.simbiot.endorphin.node.ENDLiteral;
import dev.simbiot.endorphin.node.ENDNode;
import dev.simbiot.endorphin.node.ENDNode.Visitor;
import dev.simbiot.endorphin.node.ENDPartial;
import dev.simbiot.endorphin.node.ENDPartialStatement;
import dev.simbiot.endorphin.node.ENDProgram;
import dev.simbiot.endorphin.node.ENDTemplate;
import dev.simbiot.endorphin.node.ENDVariable;
import dev.simbiot.endorphin.node.ENDVariableStatement;
import dev.simbiot.endorphin.node.PlainStatement;
import dev.simbiot.runtime.HTML;
import static dev.simbiot.compiler.BuiltIn.GET_SLOT;
import static dev.simbiot.compiler.BuiltIn.SLOT_DEFAULT_KEY;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinNodeVisitor implements Visitor {
    private final String id;
    private final String hash;
    private final ProgramBuilder builder;
    private final Map<String, Literal> componentIds;

    public EndorphinNodeVisitor(String id, String hash) {
        this.id = id;
        this.hash = hash;
        this.builder = new ProgramBuilder();
        this.componentIds = new HashMap<>();
    }

    public EndorphinNodeVisitor(EndorphinNodeVisitor parent) {
        this.id = parent.id;
        this.hash = parent.hash;
        this.builder = new ProgramBuilder();
        this.componentIds = parent.componentIds;
    }

    public ProgramBuilder accept(ENDNode[] children) {
        for (ENDNode node : children) {
            node.accept(this);
        }
        return builder;
    }

    @Override
    public void visit(ENDImport node) {
        final String name = node.getName();
        final String href = node.getHref();
        final String componentId = href
            .substring(2, href.length() - 5)
            .replace("/", ".");

        componentIds.put(name, new Literal(componentId));
    }

    @Override
    public void visit(ENDPartial node) {

    }

    @Override
    public void visit(ENDTemplate node) {
        builder.write(new CallExpression("@componentStart", new Literal(id), new Literal(hash), new Identifier("@props")), false);
        for (ENDNode child : node.getBody()) {
            child.accept(this);
        }
        builder.writeElementEnd(id);
    }

    @Override
    public void visit(ENDLiteral node) {
        final String data = node.getString();

        if (!data.trim().isEmpty()) {
            builder.write(data.replace("[\r\n\t]", ""));
        }
    }

    @Override
    public void visit(ENDProgram node) {
        builder.write(node.getExpression(), true);
    }

    @Override
    public void visit(ENDInnerHTML node) {
        builder.write(node.getExpression(), false);
    }

    @Override
    public void visit(ENDElement node) {
        if (node.isComponent()) {
            writeComponent(node);
        } else if ("slot".equals(node.getName().getName())) {
            writeSlot(node);
        } else {
            writeElement(node);
        }
    }

    @Override
    public void visit(ENDVariableStatement node) {
        List<VariableDeclarator> declarators = new ArrayList<>();
        for (ENDVariable variable : node.getVariables()) {
            declarators.add(new VariableDeclarator(variable.getName(), variable.getValue().getExpression()));
        }
        builder.append(new VariableDeclaration(Kind.CONST, declarators));
    }

    @Override
    public void visit(ENDIfStatement node) {
        builder.append(new IfStatement(node.getTest(), inner(node.getConsequent())));
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

        builder.append(result);
    }

    @Override
    public void visit(ENDForEachStatement node) {
        final String iteratorName = "iterator" + System.nanoTime();
        final Expression expression = ((ExpressionStatement) node.getSelect().getBody()[0]).getExpression();
        builder.append(new VariableDeclaration(node.getIndexName(), new Literal(0)));
        builder.append(new VariableDeclaration(iteratorName, new CallExpression("@iterator", expression)));
        builder.append(new WhileStatement(
            new CallExpression(iteratorName, "hasNext"),
            new BlockStatement(
                new VariableDeclaration(node.getValueName(), new CallExpression(iteratorName, "next")),
                inner(node.getBody()),
                new ExpressionStatement(UpdateExpression.increment(new Identifier(node.getIndexName())))
            )
        ));
    }

    @Override
    public void visit(ENDPartialStatement node) {

    }

    private Statement inner(ENDNode... children) {
        return new EndorphinNodeVisitor(this)
            .accept(children)
            .statement();
    }

    private void writeElement(ENDElement node) {
        final String name = node.getName().getName();

        writeElementStart(name, node.getAttributes());
        if (node.hasChildren() && HTML.isSelfClosing(name)) {
            builder.write("/>");
        } else {
            builder.write(">");
            for (ENDNode child : node.getBody()) {
                child.accept(this);
            }
            builder.writeElementEnd(name);
        }
    }

    private void writeComponent(ENDElement node) {
        final Literal componentId = componentIds.get(node.getName().getName());
        final List<Property> slots = new ArrayList<>();
        final List<ENDNode> defaultSlotNodes = new ArrayList<>();

        for (ENDNode child : node.getBody()) {
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
            final Statement body = inner(defaultSlotNodes.toArray(new ENDNode[0]));
            slots.add(new Property(new Identifier(SLOT_DEFAULT_KEY), new ArrowFunctionExpression(body)));
        }

        final List<Property> props = new ArrayList<>();
        props.add(new Property("@hash", new Literal(hash)));
        for (ENDAttribute attr : node.getAttributes()) {
            props.add(new Property((Identifier) attr.getName(), attr.getValue().getExpression()));
        }

        builder.call(new Identifier("@component"), componentId, new ObjectExpression(props), new ObjectExpression(slots));
    }

    private void writeSlot(ENDElement node) {
        final Expression key = new Literal(getSlotName(node, "name").orElse(SLOT_DEFAULT_KEY));
        final Expression value = node.getBody().length > 0 ?
            new ArrowFunctionExpression(inner(node.getBody())) :
            new Identifier("@empty-slot");

        writeElementStart("slot", node.getAttributes());
        builder.write(">");
        builder.call(new MemberExpression(new CallExpression(GET_SLOT, key, value), new Identifier("render")));
        builder.writeElementEnd("slot");
    }

    private void writeElementStart(String name, ENDAttribute[] attributes) {
        builder.write("<" + name + " " + hash);

        for (ENDAttribute attribute : attributes) {
            builder.write(" " + ((Identifier) attribute.getName()).getName() + "=\"");
            builder.write(attribute.getValue().getExpression(), true);
            builder.write("\"");
        }
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
