package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.SourceType;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.EmptyStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.runtime.HTML;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ProgramBuilder {
    private final StringBuilder current;
    private final List<Statement> target;

    public ProgramBuilder() {
        this.current = new StringBuilder();
        this.target = new ArrayList<>();
    }

    public void write(String value) {
        current.append(value);
    }

    public void write(Expression expression, boolean escape) {
        if (expression instanceof Literal) {
            String value = ((Literal) expression).getString();
            if (value != null && !value.isEmpty()) {
                write(escape ? HTML.escape(value) : value);
            }
            return;
        }

        call(BuiltIn.WRITE, escape ? BuiltIn.escape(expression) : expression);
    }

    public void writeElementEnd(String name) {
        write("</" + name + ">");
    }

    public void call(Expression callee, Expression... args) {
        append(new ExpressionStatement(new CallExpression(callee, args)));
    }

    public void append(Statement statement) {
        flush();
        target.add(statement);
    }

    public Statement statement() {
        flush();

        if (target.isEmpty()) {
            return new EmptyStatement();
        }

        if (target.size() == 1) {
            return target.get(0);
        }

        return new BlockStatement(target);
    }

    public Program program() {
        flush();
        return new Program(SourceType.SCRIPT, target);
    }

    private void flush() {
        if (current.length() > 0) {
            String value = current.toString();
            current.setLength(0);
            call(BuiltIn.WRITE, new Literal(value));
        }
    }
}
