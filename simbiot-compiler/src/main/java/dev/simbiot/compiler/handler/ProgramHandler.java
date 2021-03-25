package dev.simbiot.compiler.handler;

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
public class ProgramHandler implements Handler<Program> {
    private final ExpressionsHandler expressions;

    public ProgramHandler() {
        this.expressions = new ExpressionsHandler();
    }

    @Override
    public void handle(CompilerContext ctx, Program program, HandleResult result) {
        for (Statement statement : program.getBody()) {
            process(ctx, statement, result);
        }
        result.append(MethodReturn.VOID);
    }

    private void process(CompilerContext ctx, Statement statement, HandleResult result) {
        statement.accept(new StatementVisitor() {
            @Override
            public void visit(BlockStatement statement) {
                for (Statement child : statement.getBody()) {
                    process(ctx, child, result);
                }
            }

            @Override
            public void visit(VariableDeclaration statement) {
                for (VariableDeclarator declarator : statement.getDeclarations()) {
                    final HandleResult res = new HandleResult();
                    expressions.handle(ctx, declarator.getInit(), res);
                    append(res.build());
                    append(ctx.store(declarator.getId().getName(), res.getReturnType()));
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
                append(new CallExpression("toBoolean"));
                append(new IfFalse(ifLabel));
                process(ctx, statement.getConsequent(), result);

                if (statement.getAlternate() == null) {
                    append(new JumpTarget(ifLabel));
                } else {
                    append(new GoTo(elseLabel));
                    append(new JumpTarget(ifLabel));
                    process(ctx, statement.getAlternate(), result);
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
                process(ctx, statement.getBody(), result);
                append(new GoTo(loopStart));
                append(new JumpTarget(loopEnd));
            }

            private void append(Expression expression) {
                expressions.handle(ctx, expression, result);
            }

            private void append(HandleResult otherResult) {
                result.append(otherResult);
            }

            private void append(StackManipulation manipulation) {
                result.append(manipulation);
            }
        });
    }
}
