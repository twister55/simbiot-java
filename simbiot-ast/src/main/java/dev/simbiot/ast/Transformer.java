package dev.simbiot.ast;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.BreakStatement;
import dev.simbiot.ast.statement.ContinueStatement;
import dev.simbiot.ast.statement.DebuggerStatement;
import dev.simbiot.ast.statement.DoWhileStatement;
import dev.simbiot.ast.statement.EmptyStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.ForInStatement;
import dev.simbiot.ast.statement.ForOfStatement;
import dev.simbiot.ast.statement.ForStatement;
import dev.simbiot.ast.statement.IfStatement;
import dev.simbiot.ast.statement.LabeledStatement;
import dev.simbiot.ast.statement.ReturnStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.Statement.Visitor;
import dev.simbiot.ast.statement.SwitchStatement;
import dev.simbiot.ast.statement.ThrowStatement;
import dev.simbiot.ast.statement.TryStatement;
import dev.simbiot.ast.statement.WhileStatement;
import dev.simbiot.ast.statement.WithStatement;
import dev.simbiot.ast.statement.declaration.ClassDeclaration;
import dev.simbiot.ast.statement.declaration.FunctionDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.module.ExportAllDeclaration;
import dev.simbiot.ast.statement.module.ExportDefaultDeclaration;
import dev.simbiot.ast.statement.module.ExportNamedDeclaration;
import dev.simbiot.ast.statement.module.ImportDeclaration;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Transformer implements Visitor {
    private final List<Statement> statements;
    
    public Transformer() {
        this.statements = new ArrayList<>();
    }

    public void transform(Program program) {
        program.accept(this);
    }

    public List<Statement> statements() {
        return statements;
    }

    @Override
    public void visit(BlockStatement statement) {
        append(statement);
    }

    @Override
    public void visit(BreakStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ContinueStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ClassDeclaration statement) {
        append(statement);
    }

    @Override
    public void visit(DebuggerStatement statement) {
        append(statement);
    }

    @Override
    public void visit(DoWhileStatement statement) {
        append(statement);
    }

    @Override
    public void visit(EmptyStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ExportAllDeclaration statement) {
        append(statement);
    }

    @Override
    public void visit(ExportDefaultDeclaration statement) {
        append(statement);
    }

    @Override
    public void visit(ExportNamedDeclaration statement) {
        append(statement);
    }

    @Override
    public void visit(ExpressionStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ForInStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ForOfStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ForStatement statement) {
        append(statement);
    }

    @Override
    public void visit(FunctionDeclaration statement) {
        append(statement);
    }

    @Override
    public void visit(IfStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ImportDeclaration statement) {
        append(statement);
    }

    @Override
    public void visit(LabeledStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ReturnStatement statement) {
        append(statement);
    }

    @Override
    public void visit(SwitchStatement statement) {
        append(statement);
    }

    @Override
    public void visit(ThrowStatement statement) {
        append(statement);
    }

    @Override
    public void visit(TryStatement statement) {
        append(statement);
    }

    @Override
    public void visit(VariableDeclaration statement) {
        append(statement);
    }

    @Override
    public void visit(WhileStatement statement) {
        append(statement);
    }

    @Override
    public void visit(WithStatement statement) {
        append(statement);
    }

    protected void append(Statement statement) {
        statements.add(statement);
    }

}
