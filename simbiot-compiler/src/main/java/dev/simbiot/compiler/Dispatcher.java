package dev.simbiot.compiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.runtime.HTML;
import dev.simbiot.runtime.Objects;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;
import net.bytebuddy.description.method.MethodDescription.InGenericShape;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.implementation.bytecode.collection.ArrayFactory.forType;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Dispatcher {
    public static final String CONSTANTS_FIELD_NAME = "CONSTANTS";
    public static final String COMPONENTS_FIELD_NAME = "components";

    public static final Expression WRITER = new Identifier("@writer");
    public static final Expression WRITER_WRITE = new MemberExpression(WRITER, new Identifier("write"));
    public static final Expression PROPS = new Identifier("@props");
    public static final Expression SLOTS = new Identifier("@slots");
    public static final Expression GET_SLOT = new MemberExpression(SLOTS, new Identifier("getOrDefault"));

    private final ExpressionsResolver expressionsResolver;
    private final Map<String, CallHandler> mapping;

    public Dispatcher(ExpressionsResolver expressionsResolver) {
        this.expressionsResolver = expressionsResolver;
        this.mapping = new HashMap<>();
        bindAliases();
        bind(HTML.class, Objects.class);
    }

    public Chunk dispatch(CompilerContext ctx, CallExpression call) {
        final Expression callee = call.getCallee();
        final Expression[] args = call.getArguments();

        if (callee instanceof Identifier) {
            return dispatch(ctx, (Identifier) callee, args);
        }

        if (callee instanceof MemberExpression) {
            return dispatch(ctx, (MemberExpression) callee, args);
        }

        throw new IllegalArgumentException(callee.getType() + " as callee is not supported");
    }

    private Chunk dispatch(CompilerContext ctx, Identifier callee, Expression[] args) {
        final CallHandler handler = mapping.get(callee.getName());
        if (handler == null) {
            throw new IllegalStateException(callee.getName() + " is not a declared");
        }
        return handler.handle(ctx, args);
    }

    private Chunk dispatch(CompilerContext ctx, MemberExpression callee, Expression[] args) {
        final Chunk result = expressionsResolver.resolve(ctx, callee.getObject());
        final Identifier methodId = (Identifier) callee.getProperty();
        final List<Chunk> arguments = expressionsResolver.resolve(ctx, args);
        final MethodList<InGenericShape> methods = result.type()
            .getDeclaredMethods()
            .filter(named(methodId.getName()));

        if (methods.size() == 1) {
            return invoke(result, methods.getOnly(), arguments);
        }

        MethodList<InGenericShape> filtered = methods.filter(takesArguments(args.length));
        if (filtered.size() == 1) {
            return invoke(result, methods.getOnly(), arguments);
        }

        filtered = methods.filter(takesArgument(0, arguments.get(0).type().asErasure()));
        if (filtered.size() == 1) {
            return invoke(result, filtered.getOnly(), arguments);
        }

        filtered = methods.filter(takesArgument(0, Object.class));
        if (filtered.size() == 1) {
            return invoke(result, filtered.getOnly(), arguments);
        }

        throw new IllegalStateException("method " + methodId.getName() + " can not be resolved");
    }

    private Chunk invoke(Chunk callee, MethodDescription method, List<Chunk> args) {
        List<StackManipulation> arguments = args.stream()
            .map(Chunk::result)
            .collect(Collectors.toList());

        if (method.isVarArgs()) {
            callee.append(forType(of(Object.class).asGenericType()).withValues(arguments));
        } else {
            for (StackManipulation argument : arguments) {
                callee.append(argument);
            }
        }

        return callee.append(MethodInvocation.invoke(method), method.getReturnType());
    }

    private void bindAliases() {
        bindAlias("@attr", (ctx, args) -> new CallExpression(
            new MemberExpression(PROPS, new Identifier(args.length == 2 ? "getOrDefault" : "get")), args
        ));
        bindAlias("@write", (ctx, args) -> {
            final Expression value = args[0];
            final Literal escape = (Literal) args[1];

            if (value instanceof Literal && !escape.isTrue()) {
                final int idx = ctx.addConstant(((Literal) value).getString());
                return new CallExpression(WRITER_WRITE, new MemberExpression("@" + CONSTANTS_FIELD_NAME, idx));
            }

            return new CallExpression(WRITER_WRITE, escape.isTrue() ? new CallExpression("@escape", value) : value);
        });
        bindAlias("@component", (ctx, args) -> {
            final int idx = ctx.addComponentId(((Literal) args[0]).getString());
            final MemberExpression callee = new MemberExpression(
                new MemberExpression("@" + COMPONENTS_FIELD_NAME, idx),
                new Identifier("render")
            );

            return new CallExpression(callee, WRITER, args[1], args[2]);
        });
        bindAlias("@slot", (ctx, args) -> new CallExpression(
            new MemberExpression(new CallExpression(GET_SLOT, args), new Identifier("render"))
        ));
    }

    private void bind(Class<?>... types) {
        for (Class<?> type : types) {
            for (Method method : type.getMethods()) {
                if (Modifier.isStatic(method.getModifiers())) {
                    mapping.put(
                        "@" + method.getName(),
                        (ctx, args) -> invoke(new Chunk(), new ForLoadedMethod(method), expressionsResolver.resolve(ctx, args))
                    );
                }
            }
        }
    }

    private void bindAlias(String name, BiFunction<CompilerContext, Expression[], CallExpression> alias) {
        mapping.put(name, (ctx, args) -> dispatch(ctx, alias.apply(ctx, args)));
    }
}
