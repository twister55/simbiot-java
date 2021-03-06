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
import dev.simbiot.ast.expression.ObjectExpression;
import dev.simbiot.ast.expression.UpdateExpression;
import dev.simbiot.ast.pattern.AssignmentPattern;
import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.IfStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.WhileStatement;
import dev.simbiot.ast.statement.declaration.FunctionDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration.Kind;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.compiler.BuiltIn;
import dev.simbiot.compiler.ProgramBuilder;
import dev.simbiot.endorphin.node.ENDAttribute;
import dev.simbiot.endorphin.node.ENDChooseCase;
import dev.simbiot.endorphin.node.ENDChooseStatement;
import dev.simbiot.endorphin.node.ENDDirective;
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
import dev.simbiot.HTML;
import static dev.simbiot.compiler.BuiltIn.PROPS_GET_OR_DEFAULT;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinNodeVisitor implements Visitor {
    private final String id;
    private final String hash;
    private final EndorphinProgramBuilder builder;
    private final Map<String, Literal> componentIds;

    public EndorphinNodeVisitor(String id, String hash) {
        this.id = id;
        this.hash = hash;
        this.builder = new EndorphinProgramBuilder(hash);
        this.componentIds = new HashMap<>();
    }

    public EndorphinNodeVisitor(EndorphinNodeVisitor parent) {
        this.id = parent.id;
        this.hash = parent.hash;
        this.builder = new EndorphinProgramBuilder(parent.hash);
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
        final Statement body = new BlockStatement(inner(node.getBody()));
        final Pattern[] params = Arrays.stream(node.getParams())
            .map(param -> new AssignmentPattern((Identifier) param.getName(), param.getValue()))
            .toArray(Pattern[]::new);

        builder.append(new FunctionDeclaration("partial__" + node.getId(), body, params));
    }

    @Override
    public void visit(ENDTemplate node) {
        builder.writeElementStart(id);
        builder.write(" " + hash + "-host");
        builder.write(new CallExpression("@attrs", new Identifier("@props")), false);
        builder.write(">");
        for (ENDNode child : node.getBody()) {
            child.accept(this);
        }
        builder.writeElementEnd(id);
    }

    @Override
    public void visit(ENDLiteral node) {
        final String data = node.getString();

        if (data != null && !data.trim().isEmpty()) {
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
        final String partialImplId = "partial__" + node.getId() + "__impl";
        final CallExpression partialImpl = new CallExpression(
            BuiltIn.PROPS_GET_OR_DEFAULT,
            new Literal("partial:" + node.getId()),
            new Identifier("partial__" + node.getId())
        );
        final List<Property> args = new ArrayList<>();
        for (ENDAttribute attr : node.getParams()) {
            args.add(new Property((Identifier) attr.getName(), attr.getValue()));
        }

        builder.append(new VariableDeclaration(partialImplId, partialImpl));
        builder.call(new Identifier(partialImplId), new ObjectExpression(args));
    }

    private Statement inner(ENDNode... children) {
        return new EndorphinNodeVisitor(this)
            .accept(children)
            .statement();
    }

    private void writeElement(ENDElement node) {
        final String name = node.getName().getName();

        builder.writeElementStart(name);
        builder.writeAttributes(node.getProperties());
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
        final List<Property> props = new ArrayList<>();
        final List<ENDNode> defaultSlotNodes = new ArrayList<>();

        for (ENDNode child : node.getBody()) {
            if (child instanceof ENDElement) {
                final Optional<String> slotId = getSlotName((ENDElement) child, "slot");

                if (slotId.isPresent()) {
                    props.add(new Property("slot:" + slotId.get(), new ArrowFunctionExpression(inner(child))));
                    continue;
                }
            }

            defaultSlotNodes.add(child);
        }

        if (!defaultSlotNodes.isEmpty()) {
            final Statement body = inner(defaultSlotNodes.toArray(new ENDNode[0]));
            props.add(new Property(new Identifier("slot:default"), new ArrowFunctionExpression(body)));
        }

        props.add(new Property(new Identifier(hash), Literal.NULL));
        for (ENDAttribute attr : node.getAttributes()) {
            props.add(new Property((Identifier) attr.getName(), attr.getValue()));
        }

        for (ENDDirective directive : node.getDirectives()) {
            if (directive.getPrefix().equals("partial")) {
                final Literal value = (Literal) directive.getValue();
                props.add(new Property("partial:" + directive.getName(), new Identifier("partial__" + value.getString())));
            }
        }

        builder.call(new Identifier("@component"), componentId, new ObjectExpression(props));
    }

    private void writeSlot(ENDElement node) {
        final Identifier id = new Identifier("slot" + System.nanoTime());
        final Expression key = new Literal("slot:" + getSlotName(node, "name").orElse("default"));
        final Expression value = new ArrowFunctionExpression(inner(node.getBody()));
        final Expression impl = new CallExpression(PROPS_GET_OR_DEFAULT, key, value);

        builder.writeElementStart("slot");
        builder.writeAttributes(node.getProperties());
        builder.write(">");
        builder.append(new VariableDeclaration(new VariableDeclarator(id, impl)));
        builder.call(id, new ObjectExpression());
        builder.writeElementEnd("slot");
    }

    private Optional<String> getSlotName(ENDElement element, String attrName) {
        return Arrays.stream(element.getAttributes())
            .filter(attribute -> attribute.getName() instanceof Identifier)
            .filter(attribute -> ((Identifier) attribute.getName()).getName().equals(attrName))
            .map(attribute -> {
                final Expression value = attribute.getValue();
                if (value instanceof Literal) {
                    return ((Literal) value).getString();
                }
                throw new UnsupportedNodeException(value, "value of slot");
            })
            .findFirst();
    }
}
