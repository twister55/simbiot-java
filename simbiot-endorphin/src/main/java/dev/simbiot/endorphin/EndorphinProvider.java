package dev.simbiot.endorphin;

import java.util.Arrays;

import dev.simbiot.ast.ProgramLoader;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.BuiltIn;
import dev.simbiot.compiler.bytecode.StackChunk;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.CompilingProvider;
import dev.simbiot.compiler.ExpressionResolver;
import dev.simbiot.compiler.MethodBindings;
import dev.simbiot.endorphin.node.expression.IdentifierWithContext;
import dev.simbiot.runtime.END;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinProvider extends CompilingProvider {

    public EndorphinProvider() {
        this(new EndorphinLoader());
    }

    public EndorphinProvider(ProgramLoader<?> loader) {
        super(loader);
        getMethodBindings().bindInternal(END.class);
    }

    public EndorphinProvider withHelpers(Class<?>... types) {
        getMethodBindings().bind(types);
        return this;
    }

    @Override
    protected ExpressionResolver createExpressionResolver() {
        return new EndorphinExpressionResolver(getMethodBindings());
    }

    public static class EndorphinExpressionResolver extends ExpressionResolver {

        public EndorphinExpressionResolver(MethodBindings bindings) {
            super(bindings);
        }

        @Override
        public StackChunk resolve(CompilerContext ctx, Identifier expression) {
            if (expression instanceof IdentifierWithContext) {
                final IdentifierWithContext.Context context = ((IdentifierWithContext) expression).getContext();

                if (context == IdentifierWithContext.Context.PROPERTY) {
                    return resolve(ctx, new CallExpression(BuiltIn.PROPS_GET, new Literal(expression.getName())));
                }
            }

            return super.resolve(ctx, expression);
        }

        @Override
        public StackChunk resolve(CompilerContext ctx, CallExpression expression) {
            final Expression callee = expression.getCallee();

            if (callee instanceof IdentifierWithContext) {
                final IdentifierWithContext.Context context = ((IdentifierWithContext) callee).getContext();

                if (context == IdentifierWithContext.Context.HELPER) {
                    final Expression[] argsWithThis = expression.getArguments();
                    Expression[] args = Arrays.copyOfRange(argsWithThis, 1, argsWithThis.length);
                    return super.resolve(ctx, new CallExpression(callee, args));
                }
            }

            return super.resolve(ctx, expression);
        }
    }
}
