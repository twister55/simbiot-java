package dev.simbiot.compiler.program;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.IfStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.StatementVisitor;
import dev.simbiot.ast.statement.WhileStatement;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfFalse;
import dev.simbiot.compiler.bytecode.JumpTarget;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.Label;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ProgramHandler {
    private final Executor executor;

    public ProgramHandler() {
        this.executor = new Executor();
    }

    public StackManipulation handle(CompilerContext ctx, Program program) {
        final Chunk result = new Chunk();
        for (Statement statement : program.getBody()) {
            handle(ctx, statement, result);
        }
        result.append(MethodReturn.VOID);
        return result.build();
    }

    private void handle(CompilerContext ctx, Statement statement, Chunk result) {
        statement.accept(new Visitor(ctx, result));
    }

    private class Visitor extends StatementVisitor {
        private final CompilerContext ctx;
        private final Chunk result;

        private Visitor(CompilerContext ctx, Chunk result) {
            this.ctx = ctx;
            this.result = result;
        }

        @Override
        public void visit(BlockStatement statement) {
            statement.forEach(this::append);
        }

        @Override
        public void visit(VariableDeclaration statement) {
            for (VariableDeclarator declarator : statement.getDeclarations()) {
                append(ctx.store(declarator.getId().getName(), executor.execute(ctx, declarator.getInit())));
            }
        }

        @Override
        public void visit(ExpressionStatement statement) {
            append(statement.getExpression());
        }

        @Override
        public void visit(IfStatement statement) {
            final Label ifLabel = new Label();
            final Label elseLabel = new Label();

            append(statement.getTest());
            append(new CallExpression("@is"));
            append(new IfFalse(ifLabel));
            append(statement.getConsequent());

            if (statement.getAlternate() == null) {
                append(new JumpTarget(ifLabel));
            } else {
                append(new GoTo(elseLabel));
                append(new JumpTarget(ifLabel));
                append(statement.getAlternate());
                append(new JumpTarget(elseLabel));
            }
        }

        @Override
        public void visit(WhileStatement statement) {
            final Label loopStart = new Label();
            final Label loopEnd = new Label();

            append(new JumpTarget(loopStart));
            append(statement.getTest());
            append(new IfFalse(loopEnd));
            append(statement.getBody());
            append(new GoTo(loopStart));
            append(new JumpTarget(loopEnd));
        }

        private void append(Statement statement) {
            handle(ctx, statement, result);
        }

        private void append(Expression expression) {
            result.append(executor.execute(ctx, expression));
        }

        private void append(StackManipulation manipulation) {
            result.append(manipulation);
        }
    }
}
