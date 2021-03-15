package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.Transformer;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class ProgramTransformer extends Transformer {
    public final List<String> parts;

    public ProgramTransformer() {
        this.parts = new ArrayList<>();
    }

    public List<String> parts() {
        return parts;
    }

    @Override
    public void visit(ExpressionStatement statement) {
        final Expression expression = statement.getExpression();

        if (expression instanceof CallExpression) {
            append(new ExpressionStatement(transform((CallExpression) expression)));
        } else {
            append(statement);
        }
    }

    @Override
    public void visit(VariableDeclaration statement) {
        final VariableDeclarator[] declarations = statement.getDeclarations();

        for (int i = 0; i < declarations.length; i++) {
            final VariableDeclarator declarator = declarations[i];
            final Identifier id = declarator.getId();
            final Expression init = declarator.getInit();

            if (init instanceof CallExpression) {
                declarations[i] = new VariableDeclarator(id, transform((CallExpression) init));
            }
        }

        append(new VariableDeclaration(statement.getKind(), declarations));
    }

    private CallExpression transform(CallExpression expression) {
        final Identifier callee = (Identifier) expression.getCallee();
        final Expression[] arguments = expression.getArguments();

        switch (callee.getName()) {
            case "attr":
                return attr(arguments);

            case "debug":
                return debug(arguments);

            case "write":
                return write(arguments);

            default:
                return expression;
        }
    }

    private CallExpression attr(Expression[] arguments) {
        return new CallExpression(new MemberExpression("props", "get"), arguments);
    }

    private CallExpression debug(Expression[] arguments) {
        return new CallExpression(new MemberExpression("logger", "debug"), arguments);
    }

    private CallExpression write(Expression[] arguments) {
        final MemberExpression callee = new MemberExpression("writer", "write");
        final Expression value = arguments[0];
        final Literal escape = (Literal) arguments[1];

        if (!escape.isTrue() && value instanceof Literal) {
            final int index = parts.size();
            parts.add(((Literal) value).getString());
            return new CallExpression(callee, new MemberExpression("$$PARTS", index));
        }

        return new CallExpression(callee, arguments);
    }
}
