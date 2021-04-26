package dev.simbiot.compiler.bytecode;

import dev.simbiot.runtime.Objects;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.description.type.TypeDescription.Generic.OfNonGenericType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class StackChunk implements StackManipulation {
    public static final StackChunk NULL = new StackChunk(Generic.OBJECT, NullConstant.INSTANCE);

    /**
     * The {@link Objects#equals(Object, Object)} method.
     */
    public static final InDefinedShape EQUALS = new ForLoadedType(Objects.class)
        .getDeclaredMethods()
        .filter(named("equals"))
        .getOnly();

    /**
     * The {@link Objects#is(Object)} method.
     */
    public static final InDefinedShape IS = new ForLoadedType(Objects.class)
        .getDeclaredMethods()
        .filter(named("is"))
        .getOnly();

    public static StackChunk of(Class<?> type, StackManipulation manipulation) {
        return new StackChunk(ForLoadedType.of(type).asGenericType(), manipulation);
    }

    public static StackChunk forField(FieldDescription field) {
        return new StackChunk(
            field.getType(),
            new Compound(
                field.isStatic() ? Trivial.INSTANCE : MethodVariableAccess.loadThis(),
                FieldAccess.forField(field).read()
            )
        );
    }

    public static StackChunk condition(StackChunk test) {
        if (!test.type().represents(boolean.class)) {
            test.append(MethodInvocation.invoke(IS), boolean.class);
        }
        return test;
    }

    public static StackChunk negation(StackChunk value) {
        final Label ifLabel = new Label();
        final Label elseLabel = new Label();

        return StackChunk.condition(value)
            .append(new IfTrue(ifLabel))
            .append(IntegerConstant.forValue(true))
            .append(new GoTo(elseLabel))
            .append(new JumpTarget(ifLabel))
            .append(IntegerConstant.forValue(false))
            .append(new JumpTarget(elseLabel), boolean.class);
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
}
