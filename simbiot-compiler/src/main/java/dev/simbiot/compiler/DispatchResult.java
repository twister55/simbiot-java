package dev.simbiot.compiler;

import java.lang.reflect.Method;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.expression.Expression;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.InGenericShape;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class DispatchResult {
    @Nullable
    private final StackManipulation callee;
    private final MethodDescription method;
    private final Expression[] arguments;

    public DispatchResult(@Nullable StackManipulation callee, MethodDescription method, Expression[] arguments) {
        this.callee = callee;
        this.method = method;
        this.arguments = arguments;
    }

    @Nullable
    public StackManipulation getCallee() {
        return callee;
    }

    public MethodDescription getMethod() {
        return method;
    }

    public Generic getReturnType() {
        return method.getReturnType();
    }

    public Expression[] getArguments() {
        return arguments;
    }

    public static class Builder {
        @Nullable
        private StackManipulation callee;
        private MethodDescription method;
        private Expression[] arguments;

        public Builder callee(StackManipulation... callee) {
            this.callee = new StackManipulation.Compound(callee);
            return this;
        }

        public Builder callee(StackManipulation callee) {
            this.callee = callee;
            return this;
        }

        public Builder method(Method method) {
            this.method = new MethodDescription.ForLoadedMethod(method);
            return this;
        }

        public Builder method(Generic type, ElementMatcher<InGenericShape> filter) {
            this.method = type.getDeclaredMethods().filter(filter).getOnly();
            return this;
        }

        public Builder arg(Expression arg) {
            arguments = new Expression[] { arg };
            return this;
        }

        public Builder args(Expression... args) {
            arguments = args;
            return this;
        }

        public DispatchResult build() {
            if (method == null) {
                throw new IllegalStateException("method can cot be null");
            }

            if (arguments == null) {
                throw new IllegalStateException("arguments can cot be null");
            }

            return new DispatchResult(callee, method, arguments);
        }
    }
}
