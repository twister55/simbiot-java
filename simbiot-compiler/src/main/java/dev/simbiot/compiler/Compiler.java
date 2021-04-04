package dev.simbiot.compiler;

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
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfFalse;
import dev.simbiot.compiler.bytecode.JumpTarget;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.Label;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class Compiler {
    private final ExpressionsResolver expressionsResolver;

    protected Compiler() {
        this.expressionsResolver = new ExpressionsResolver();
    }

    protected Compiler(ExpressionsResolver expressionsResolver) {
        this.expressionsResolver = expressionsResolver;
    }

    protected <T> Unloaded<T> compile(CompilerContext ctx, Class<T> type, Statement... statements) {
        final Builder<T> builder = createBuilder(ctx, type);
        final ParameterList<ParameterDescription.InDefinedShape> parameters = new ForLoadedType(type)
            .getDeclaredMethods()
            .filter(isAbstract())
            .getOnly()
            .getParameters();

        ctx.init(builder.toTypeDescription(), parameters);

        return builder
            .method(isAbstract())
            .intercept(methodImplementation(ctx, implement(ctx, statements)))
            .declaredTypes(ctx.getDeclaredTypes())
            .make();
    }

    protected <T> Builder<T> createBuilder(CompilerContext ctx, Class<T> type) {
        return new ByteBuddy()
            .subclass(type, ConstructorStrategy.Default.NO_CONSTRUCTORS)
            .name(ctx.getId());
    }

    private Implementation methodImplementation(CompilerContext ctx, Chunk result) {
        return new Implementation() {
            @Override
            public ByteCodeAppender appender(Target target) {
                return (methodVisitor, implementationContext, methodDescription) -> new ByteCodeAppender.Size(
                    result.result().apply(methodVisitor, implementationContext).getMaximalSize(),
                    methodDescription.getStackSize() + ctx.getLocalVarsCount()
                );
            }

            @Override
            public InstrumentedType prepare(InstrumentedType type) {
                return type;
            }
        };
    }

    private Chunk implement(CompilerContext ctx, Statement... statements) {
        final Chunk result = new Chunk();
        for (Statement statement : statements) {
            implement(ctx, statement, result);
        }
        result.append(MethodReturn.VOID);
        return result;
    }

    private void implement(CompilerContext ctx, Statement statement, Chunk result) {
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
                append(ctx.store(declarator.getId().getName(), expressionsResolver.resolve(ctx, declarator.getInit())));
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
            implement(ctx, statement, result);
        }

        private void append(Expression expression) {
            result.append(expressionsResolver.resolve(ctx, expression));
        }

        private void append(StackManipulation manipulation) {
            result.append(manipulation);
        }
    }
}
