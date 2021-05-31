package dev.simbiot.endorphin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.BuiltIn;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.CompilingProvider;
import dev.simbiot.compiler.MethodBindings;
import dev.simbiot.compiler.bytecode.StackChunk;
import dev.simbiot.compiler.expression.CallExpressionHandler;
import dev.simbiot.compiler.expression.ExpressionResolver;
import dev.simbiot.compiler.expression.IdentifierHandler;
import dev.simbiot.endorphin.node.expression.IdentifierWithContext;
import dev.simbiot.endorphin.node.expression.IdentifierWithContext.Context;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinProvider extends CompilingProvider {
    private final Set<Class<?>> helpers;

    public EndorphinProvider() {
        super(new EndorphinLoader());
        this.helpers = new HashSet<>();
    }

    public EndorphinProvider withHelpers(Class<?>... helpers) {
        this.helpers.addAll(Arrays.asList(helpers));
        return this;
    }

    @Override
    protected MethodBindings createMethodBindings() {
        MethodBindings bindings = super.createMethodBindings();
        for (Class<?> helper : helpers) {
            bindings.bind(helper);
        }
        return bindings;
    }

    @Override
    protected ExpressionResolver createExpressionResolver() {
        return super.createExpressionResolver()
            .withHandler("Identifier", new IdentifierHandler() {
                @Override
                public StackChunk handle(CompilerContext ctx, Identifier expression) {
                    if (expression instanceof IdentifierWithContext) {
                        final IdentifierWithContext id = (IdentifierWithContext) expression;

                        if (id.getContext() == Context.PROPERTY) {
                            return ctx.resolve(new CallExpression(BuiltIn.PROPS_GET, new Literal(id.getName())));
                        }
                    }

                    return super.handle(ctx, expression);
                }
            })
            .withHandler("CallExpression", new CallExpressionHandler() {
                @Override
                public StackChunk handle(CompilerContext ctx, CallExpression expression) {
                    final Expression callee = expression.getCallee();

                    if (callee instanceof IdentifierWithContext) {
                        final IdentifierWithContext id = (IdentifierWithContext) callee;

                        if (id.getContext() == Context.HELPER) {
                            final Expression[] argsWithThis = expression.getArguments();
                            Expression[] args = Arrays.copyOfRange(argsWithThis, 1, argsWithThis.length);
                            return super.handle(ctx, new CallExpression(callee, args));
                        }
                    }

                    return super.handle(ctx, expression);
                }
            });
    }
}
