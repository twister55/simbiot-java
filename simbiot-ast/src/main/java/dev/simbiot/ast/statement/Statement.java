package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;
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
@JsonSubTypes(value = {
    @Type(value = BlockStatement.class, name = "BlockStatement"),
    @Type(value = BreakStatement.class, name = "BreakStatement"),
    @Type(value = ContinueStatement.class, name = "ContinueStatement"),
    @Type(value = ClassDeclaration.class, name = "ClassDeclaration"),
    @Type(value = DebuggerStatement.class, name = "DebuggerStatement"),
    @Type(value = DoWhileStatement.class, name = "DoWhileStatement"),
    @Type(value = EmptyStatement.class, name = "EmptyStatement"),
    @Type(value = ExportAllDeclaration.class, name = "ExportAllDeclaration"),
    @Type(value = ExportDefaultDeclaration.class, name = "ExportDefaultDeclaration"),
    @Type(value = ExportNamedDeclaration.class, name = "ExportNamedDeclaration"),
    @Type(value = ExpressionStatement.class, name = "ExpressionStatement"),
    @Type(value = ForInStatement.class, name = "ForInStatement"),
    @Type(value = ForOfStatement.class, name = "ForOfStatement"),
    @Type(value = ForStatement.class, name = "ForStatement"),
    @Type(value = FunctionDeclaration.class, name = "FunctionDeclaration"),
    @Type(value = IfStatement.class, name = "IfStatement"),
    @Type(value = ImportDeclaration.class, name = "ImportDeclaration"),
    @Type(value = LabeledStatement.class, name = "LabeledStatement"),
    @Type(value = ReturnStatement.class, name = "ReturnStatement"),
    @Type(value = SwitchStatement.class, name = "SwitchStatement"),
    @Type(value = ThrowStatement.class, name = "ThrowStatement"),
    @Type(value = TryStatement.class, name = "TryStatement"),
    @Type(value = VariableDeclaration.class, name = "VariableDeclaration"),
    @Type(value = WhileStatement.class, name = "WhileStatement"),
    @Type(value = WithStatement.class, name = "WithStatement")
})
public interface Statement extends Node {

    void accept(Visitor visitor);

    static Statement wrap(Statement[] statements) {
        if (statements == null) {
            return new EmptyStatement();
        }

        if (statements.length == 1) {
            return statements[0];
        }

        return new BlockStatement(statements);
    }

    interface Visitor {

        void visit(BlockStatement statement);

        void visit(BreakStatement statement);

        void visit(ContinueStatement statement);

        void visit(ClassDeclaration statement);

        void visit(DebuggerStatement statement);

        void visit(DoWhileStatement statement);

        void visit(EmptyStatement statement);

        void visit(ExportAllDeclaration statement);

        void visit(ExportDefaultDeclaration statement);

        void visit(ExportNamedDeclaration statement);

        void visit(ExpressionStatement statement);

        void visit(ForInStatement statement);

        void visit(ForOfStatement statement);

        void visit(ForStatement statement);

        void visit(FunctionDeclaration statement);

        void visit(IfStatement statement);

        void visit(ImportDeclaration statement);

        void visit(LabeledStatement statement);

        void visit(ReturnStatement statement);

        void visit(SwitchStatement statement);

        void visit(ThrowStatement statement);

        void visit(TryStatement statement);

        void visit(VariableDeclaration statement);

        void visit(WhileStatement statement);

        void visit(WithStatement statement);
    }
}
