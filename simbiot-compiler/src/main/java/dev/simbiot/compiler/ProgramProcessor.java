package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.IfStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.StatementVisitor;
import dev.simbiot.ast.statement.WhileStatement;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.compiler.bytecode.Constant;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfFalse;
import dev.simbiot.compiler.bytecode.JumpTarget;
import dev.simbiot.compiler.bytecode.ObjectAccess;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.Label;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ProgramProcessor {
    private final List<StackManipulation> manipulations;
    private final Dispatcher dispatcher;

    public ProgramProcessor() {
        this.manipulations = new ArrayList<>();
        this.dispatcher = new Dispatcher();
    }

    public Compound process(ProcessContext ctx, Program program) {
        for (Statement statement : program.getBody()) {
            append(ctx, statement);
        }
        append(MethodReturn.VOID);
        return result();
    }

    private Compound result() {
        List<StackManipulation> result = new ArrayList<>(manipulations);
        manipulations.clear();
        return new Compound(result);
    }

    private void append(ProcessContext ctx, Statement statement) {
        statement.accept(new StatementVisitor() {
            @Override
            public void visit(BlockStatement statement) {
                for (Statement child : statement.getBody()) {
                    child.accept(this);
                }
            }

            @Override
            public void visit(VariableDeclaration statement) {
                for (VariableDeclarator declarator : statement.getDeclarations()) {
                    final String name = declarator.getId().getName();
                    final Expression init = declarator.getInit();

                    if (init instanceof CallExpression) {
                        append(ctx.store(name, dispatch(ctx, init).getReturnType()));
                    } else {
                        append(ctx, init);
                        append(ctx.store(name));
                    }
                }
            }

            @Override
            public void visit(ExpressionStatement statement) {
                append(ctx, statement.getExpression());
            }

            @Override
            public void visit(IfStatement statement) {
                final Label ifLabel = new Label();
                final Label elseLabel = new Label();

                append(ctx, statement.getTest());
                dispatch(ctx, new CallExpression("toBoolean"));
                append(new IfFalse(ifLabel));
                append(ctx, statement.getConsequent());
                if (statement.getAlternate() == null) {
                    append(new JumpTarget(ifLabel));
                } else {
                    append(new GoTo(elseLabel));
                    append(new JumpTarget(ifLabel));
                    append(ctx, statement.getAlternate());
                    append(new JumpTarget(elseLabel));
                }
            }

            @Override
            public void visit(WhileStatement statement) {
                final Label loopStart = new Label();
                final Label loopEnd = new Label();

                append(new JumpTarget(loopStart));
                append(ctx, statement.getTest());
                append(new IfFalse(loopEnd));
                append(ctx, statement.getBody());
                append(new GoTo(loopStart));
                append(new JumpTarget(loopEnd));
            }
        });
    }

    private void append(ProcessContext ctx, Expression[] expressions) {
        for (Expression arg : expressions) {
            append(ctx, arg);
        }
    }

    private void append(ProcessContext ctx, @Nullable Expression expression) {
        if (expression == null) {
            return;
        }

        expression.accept(new ExpressionVisitor() {
            @Override
            public void visit(Literal expression) {
                append(new Constant(expression));
            }

            @Override
            public void visit(Identifier expression) {
                append(ctx.get(expression.getName()));
            }

            @Override
            public void visit(MemberExpression expression) {
                Expression obj = expression.getObject();
                Expression prop = expression.getProperty();

                if (obj instanceof Identifier) {
                    final String name = ((Identifier) obj).getName();

                    if ("#ctx".equals(name)) { // FIXME
                        append(ctx.get(expression.getLeadingComments()[0].getValue()));
                    } else if (ctx.getType(name).isArray()) {
                        append(ctx.get(name));
                        append(ctx, prop);
                        append(ArrayAccess.REFERENCE.load());
                    }
                } else {
                    append(ctx, obj);
                    append(new ObjectAccess(prop));
                }
            }

            @Override
            public void visit(CallExpression expression) {
                dispatch(ctx, expression);
            }
        });
    }

    private DispatchResult dispatch(ProcessContext ctx, Expression expression) {
        final DispatchResult result = dispatcher.dispatch(ctx, (CallExpression) expression);
        append(result.getCallee());
        append(ctx, result.getArguments());
        append(MethodInvocation.invoke(result.getMethod()));
        return result;
    }

    private void append(@Nullable StackManipulation manipulation) {
        if (manipulation != null) {
            manipulations.add(manipulation);
        }
    }
}
