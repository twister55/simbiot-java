package dev.simbiot.compiler.bytecode;

import java.util.Collections;
import java.util.List;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.description.type.TypeDescription.Generic.OfNonGenericType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import static net.bytebuddy.implementation.bytecode.collection.ArrayFactory.forType;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class StackChunk implements StackManipulation {
    public static final StackChunk NULL = new StackChunk(Generic.OBJECT, NullConstant.INSTANCE);

    public static StackChunk of(Class<?> type, StackManipulation manipulation) {
        return new StackChunk(ForLoadedType.of(type).asGenericType(), manipulation);
    }

    public static StackChunk call(MethodDescription method) {
        return call(method, Collections.emptyList());
    }

    public static StackChunk call(MethodDescription method, List<StackChunk> arguments) {
        return new StackChunk().invoke(method, arguments);
    }

    private Generic type;
    private StackManipulation manipulation;

    public StackChunk() {
        this.type = Generic.VOID;
        this.manipulation = Trivial.INSTANCE;
    }

    public StackChunk(Class<?> type, StackManipulation manipulation) {
        this(ForLoadedType.of(type).asGenericType(), manipulation);
    }

    public StackChunk(Generic type, StackManipulation manipulation) {
        this.type = type;
        this.manipulation = manipulation;
    }

    @Override
    public boolean isValid() {
        return manipulation != null;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        return manipulation.apply(methodVisitor, implementationContext);
    }

    public StackChunk as(Class<?> type) {
        this.type = ForLoadedType.of(type).asGenericType();
        return this;
    }

    public StackChunk as(Generic type) {
        this.type = type;
        return this;
    }

    public Generic type() {
        return type;
    }

    public StackChunk append(StackChunk other) {
        return append(other.manipulation, other.type);
    }

    public StackChunk append(StackManipulation manipulation) {
        return append(manipulation, Generic.OBJECT);
    }

    public StackChunk append(StackManipulation manipulation, Generic type) {
        this.type = type;
        this.manipulation = this.manipulation == null ? manipulation : new Compound(this.manipulation, manipulation);
        return this;
    }

    public StackChunk append(StackManipulation manipulation, Class<?> type) {
        return append(manipulation, OfNonGenericType.ForLoadedType.of(type));
    }

    public StackChunk invoke(String method, List<StackChunk> arguments) {
        final MethodList<MethodDescription.InGenericShape> methods = type.getDeclaredMethods()
            .filter(named(method));

        if (methods.size() == 1) {
            return invoke(methods.getOnly(), arguments);
        }

        MethodList<MethodDescription.InGenericShape> filtered = methods.filter(takesArguments(arguments.size()));
        if (filtered.size() == 1) {
            return invoke(methods.getOnly(), arguments);
        }

        filtered = methods.filter(takesArgument(0, arguments.get(0).type().asErasure()));
        if (filtered.size() == 1) {
            return invoke(filtered.getOnly(), arguments);
        }

        filtered = methods.filter(takesArgument(0, Object.class));
        if (filtered.size() == 1) {
            return invoke(filtered.getOnly(), arguments);
        }

        throw new IllegalStateException("method " + method + " can not be resolved");
    }

    private StackChunk invoke(MethodDescription method, List<StackChunk> arguments) {
        if (method.isVarArgs()) {
            append(forType(Generic.OBJECT).withValues(arguments));
        } else {
            for (final StackChunk arg : arguments) {
                append(arg);
            }
        }

        return append(MethodInvocation.invoke(method), method.getReturnType());
    }
}
