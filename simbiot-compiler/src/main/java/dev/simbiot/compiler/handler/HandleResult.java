package dev.simbiot.compiler.handler;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.compiler.CompilerContext;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.InGenericShape;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.matcher.ElementMatcher.Junction;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class HandleResult {
    private final List<StackManipulation> result;
    @Nullable
    private Generic returnType;

    public HandleResult() {
        this.result = new ArrayList<>();
    }

    public HandleResult append(StackManipulation manipulation) {
        result.add(manipulation);
        returnType = null;
        return this;
    }

    public HandleResult append(HandleResult other) {
        result.addAll(other.result);
        returnType = other.returnType;
        return this;
    }

    public HandleResult invoke(Generic type, Junction<MethodDescription> filter) {
        return invoke(findMethod(type, filter));
    }

    public HandleResult invoke(CompilerContext ctx, String name, Junction<MethodDescription> filter) {
        return invoke(findMethod(ctx, name, filter));
    }

    public HandleResult invoke(MethodDescription method) {
        append(MethodInvocation.invoke(method));
        returnType = method.getReturnType();
        return this;
    }

    public Generic getReturnType() {
        return returnType;
    }

    public StackManipulation build() {
        return new Compound(result);
    }

    private static InGenericShape findMethod(CompilerContext ctx, String name, Junction<MethodDescription> filter) {
        return findMethod(ctx.getType(name), filter);
    }

    private static InGenericShape findMethod(Generic type, Junction<MethodDescription> filter) {
        return type.getDeclaredMethods().filter(filter).getOnly();
    }
}
