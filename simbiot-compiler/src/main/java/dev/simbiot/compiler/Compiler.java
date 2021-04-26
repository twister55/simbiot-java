package dev.simbiot.compiler;

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
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender.Size;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class Compiler {
    private final ExpressionResolver resolver;

    protected Compiler(ExpressionResolver resolver) {
        this.resolver = resolver;
    }

    protected <T> Unloaded<T> compile(CompilerContext ctx, Class<T> type, Statement... statements) {
        final Builder<T> builder = createBuilder(ctx, type);
        final ParameterList<ParameterDescription.InDefinedShape> parameters = new ForLoadedType(type)
            .getDeclaredMethods()
            .filter(isAbstract())
            .getOnly()
            .getParameters();

        ctx.bind(builder.toTypeDescription(), parameters);

        return builder
            .method(isAbstract())
            .intercept(methodImplementation(ctx, implement(ctx, statements)))
            .declaredTypes(ctx.getDeclaredTypes())
            .make();
    }

    protected <T> Builder<T> createBuilder(CompilerContext ctx, Class<T> type) {
        return new ByteBuddy()
            .subclass(type, ConstructorStrategy.Default.NO_CONSTRUCTORS)
            .visit(new AsmVisitorWrapper.ForDeclaredMethods().writerFlags(ClassWriter.COMPUTE_FRAMES))
            .name(ctx.getId().replace("-", "_"));
    }

    private Implementation methodImplementation(CompilerContext context, StackChunk result) {
        return new Implementation() {
            @Override
            public ByteCodeAppender appender(Target target) {
                return (visitor, ctx, method) -> new Size(result.apply(visitor, ctx).getMaximalSize(), context.getStackSize());
            }

            @Override
            public InstrumentedType prepare(InstrumentedType type) {
                return type;
            }
        };
    }

    private StackChunk implement(CompilerContext ctx, Statement... statements) {
        final StackChunk result = new StackChunk();
        for (Statement statement : statements) {
            implement(ctx, statement, result);
        }
        result.append(MethodReturn.VOID);
        return result;
    }

    private void implement(CompilerContext ctx, Statement statement, StackChunk result) {
        statement.accept(new Visitor(ctx, result));
    }

    private class Visitor extends StatementVisitor {
        private final CompilerContext ctx;
        private final StackChunk result;

        private Visitor(CompilerContext ctx, StackChunk result) {
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
                append(ctx.declare(declarator.getId().getName(), resolver.resolve(ctx, declarator.getInit())));
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

            append(StackChunk.condition(resolver.resolve(ctx, statement.getTest())));
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
            result.append(resolver.resolve(ctx, expression));
        }

        private void append(StackManipulation manipulation) {
            result.append(manipulation);
        }
    }
}
