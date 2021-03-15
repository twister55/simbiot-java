package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.ast.statement.ExpressionStatement;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.StatementVisitor;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.MethodVisitor;
import static net.bytebuddy.implementation.Implementation.Context;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ProgramAppender implements ByteCodeAppender {
    private final List<Statement> statements;
    private final List<StackManipulation> manipulations;
    private int varsCount = 0;

    public ProgramAppender(List<Statement> statements) {
        this.statements = statements;
        this.manipulations = new ArrayList<>();
    }

    @Override
    public Size apply(MethodVisitor mv, Context context, MethodDescription method) {
        final ProcessContext ctx = new ProcessContext(context, method);
        final int maximalSize = process(ctx).apply(mv, context).getMaximalSize();
        final int localVariableSize = method.getStackSize() + varsCount;

        return new ByteCodeAppender.Size(maximalSize, localVariableSize);
    }

    private StackManipulation.Compound process(ProcessContext ctx) {
        for (Statement statement : statements) {
            append(ctx, statement);
        }
        append(MethodReturn.VOID);
        return new StackManipulation.Compound(manipulations);
    }

    private void append(ProcessContext ctx, Statement statement) {
        statement.accept(new StatementVisitor() {
            @Override
            public void visit(VariableDeclaration statement) {
                for (VariableDeclarator declarator : statement.getDeclarations()) {
                    varsCount++;
                    if (declarator.getInit() != null) {
                        append(ctx, declarator.getInit());
                    }
                    append(ctx.store(declarator.getId().getName()));
                }
            }

            @Override
            public void visit(ExpressionStatement statement) {
                append(ctx, statement.getExpression());
            }
        });
    }

    private void append(ProcessContext ctx, Expression[] expressions) {
        for (Expression arg : expressions) {
            append(ctx, arg);
        }
    }

    private void append(ProcessContext ctx, Expression expression) {
        expression.accept(new ExpressionVisitor() {
            @Override
            public void visit(Identifier expression) {
                append(ctx.get(expression.getName()));
            }

            @Override
            public void visit(Literal expression) {
                if (expression.isNull()) {
                    append(NullConstant.INSTANCE);
                } else if (expression.isBoolean()) {
                    append(IntegerConstant.forValue(expression.isTrue()));
                } else if (expression.isNumber()) {
                    append(IntegerConstant.forValue(expression.getInt()));
                } else {
                    append(new TextConstant(expression.getString()));
                }
            }

            @Override
            public void visit(MemberExpression expression) {
                append(ctx, expression.getObject());
                append(ctx, expression.getProperty());
                append(ArrayAccess.REFERENCE.load()); // FIXME
            }

            @Override
            public void visit(CallExpression expression) {
                final Expression callee = expression.getCallee();
                final Expression[] arguments = expression.getArguments();

                if (callee instanceof MemberExpression) {
                    final Identifier calleeObject = (Identifier) ((MemberExpression) callee).getObject();
                    final Identifier calleeProperty = (Identifier) ((MemberExpression) callee).getProperty();

                    final MethodDescription.InGenericShape method = ctx.getType(calleeObject.getName())
                            .getDeclaredMethods()
                            .filter(named(calleeProperty.getName()).and(takesArguments(arguments.length)))
                            .getOnly();

                    append(ctx.get(calleeObject.getName()));
                    append(ctx, arguments);
                    append(MethodInvocation.invoke(method));
                }
            }
        });
    }

    private void append(StackManipulation manipulation) {
        manipulations.add(manipulation);
    }
}
