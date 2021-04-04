package dev.simbiot.compiler;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Chunk {
    public static final Chunk NULL = new Chunk(NullConstant.INSTANCE, Generic.VOID);

    public static Chunk of(StackManipulation manipulation, Generic type) {
        return new Chunk(manipulation, type);
    }

    public static Chunk of(StackManipulation manipulation, Class<?> type) {
        return of(manipulation, ForLoadedType.of(type).asGenericType());
    }

    public static Chunk forField(FieldDescription field) {
        final StackManipulation manipulation = new Compound(
            field.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(),
            FieldAccess.forField(field).read()
        );
        return of(manipulation, field.getType());
    }

    @Nullable
    private StackManipulation result;
    private Generic type;

    public Chunk() {
        this.result = null;
        this.type = Generic.VOID;
    }

    private Chunk(StackManipulation result, Generic type) {
        this.result = result;
        this.type = type;
    }

    private Chunk(List<StackManipulation> result, Generic type) {
        this(new Compound(result), type);
    }

    public Generic type() {
        return type;
    }

    public StackManipulation result() {
        return result == null ? NullConstant.INSTANCE : result;
    }

    public Chunk append(Chunk other) {
        return append(other.result, other.type);
    }

    public Chunk append(StackManipulation manipulation) {
        return append(manipulation, Generic.VOID);
    }

    public Chunk append(StackManipulation manipulation, Generic type) {
        this.type = type;
        this.result = this.result == null ? manipulation : new Compound(this.result, manipulation);
        return this;
    }
}
