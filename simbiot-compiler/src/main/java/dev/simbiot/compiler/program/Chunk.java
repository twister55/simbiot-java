package dev.simbiot.compiler.program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import static net.bytebuddy.description.type.TypeDescription.Generic.OfNonGenericType.ForLoadedType.of;
import static net.bytebuddy.implementation.bytecode.collection.ArrayFactory.forType;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Chunk {
    public static final Chunk NULL = new Chunk(NullConstant.INSTANCE, Generic.VOID);

    private final List<StackManipulation> result;
    private Generic type;

    public Chunk(StackManipulation result, Generic type) {
        this(new ArrayList<>(Collections.singletonList(result)), type);
    }

    public Chunk(List<StackManipulation> result, Generic type) {
        this.result = result;
        this.type = type;
    }

    public Chunk() {
        this.result = new ArrayList<>();
        this.type = Generic.VOID;
    }

    public Generic type() {
        return type;
    }

    public StackManipulation build() {
        return new Compound(result);
    }

    public Chunk append(boolean value) {
        return append(IntegerConstant.forValue(value), boolean.class);
    }

    public Chunk append(int value) {
        return append(IntegerConstant.forValue(value), int.class);
    }

    public Chunk append(String value) {
        return append(new TextConstant(value), String.class);
    }

    public Chunk append(Chunk other) {
        result.add(other.result.size() == 1 ? other.result.get(0) : new Compound(other.result));
        type = other.type;
        return this;
    }

    public Chunk append(StackManipulation manipulation) {
        result.add(manipulation);
        type = Generic.VOID;
        return this;
    }

    public Chunk append(StackManipulation manipulation, Generic type) {
        this.result.add(manipulation);
        this.type = type;
        return this;
    }

    public Chunk invoke(MethodDescription method, List<Chunk> args) {
        List<StackManipulation> arguments = args.stream()
            .map(Chunk::build)
            .collect(Collectors.toList());

        if (method.isVarArgs()) {
            append(forType(ForLoadedType.of(Object.class).asGenericType()).withValues(arguments));
        } else {
            for (StackManipulation argument : arguments) {
                append(argument);
            }
        }

        return append(MethodInvocation.invoke(method), method.getReturnType());
    }

    private Chunk append(StackManipulation manipulation, Class<?> type) {
        return append(manipulation, of(type));
    }
}
