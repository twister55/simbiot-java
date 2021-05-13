package dev.simbiot.compiler.bytecode;

import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public enum PrimitiveBoxing implements StackManipulation {
    BOOLEAN(Boolean.class, StackSize.ZERO, "valueOf", "(Z)Ljava/lang/Boolean;"),
    BYTE(Byte.class, StackSize.ZERO, "valueOf", "(B)Ljava/lang/Byte;"),
    SHORT(Short.class, StackSize.ZERO, "valueOf", "(S)Ljava/lang/Short;"),
    CHARACTER(Character.class, StackSize.ZERO, "valueOf", "(C)Ljava/lang/Character;"),
    INTEGER(Integer.class, StackSize.ZERO, "valueOf", "(I)Ljava/lang/Integer;"),
    LONG(Long.class, StackSize.SINGLE, "valueOf", "(J)Ljava/lang/Long;"),
    FLOAT(Float.class, StackSize.ZERO, "valueOf", "(F)Ljava/lang/Float;"),
    DOUBLE(Double.class, StackSize.SINGLE, "valueOf", "(D)Ljava/lang/Double;");

    private final ForLoadedType wrapperType;
    private final Size size;
    private final String boxingMethodName;
    private final String boxingMethodDescriptor;

    PrimitiveBoxing(Class<?> wrapperType,
                    StackSize sizeDifference,
                    String boxingMethodName,
                    String boxingMethodDescriptor) {
        this.wrapperType = new ForLoadedType(wrapperType);
        this.size = sizeDifference.toDecreasingSize();
        this.boxingMethodName = boxingMethodName;
        this.boxingMethodDescriptor = boxingMethodDescriptor;
    }

    public static PrimitiveBoxing of(TypeDefinition typeDefinition) {
        if (typeDefinition.represents(boolean.class)) {
            return BOOLEAN;
        } else if (typeDefinition.represents(byte.class)) {
            return BYTE;
        } else if (typeDefinition.represents(short.class)) {
            return SHORT;
        } else if (typeDefinition.represents(char.class)) {
            return CHARACTER;
        } else if (typeDefinition.represents(int.class)) {
            return INTEGER;
        } else if (typeDefinition.represents(long.class)) {
            return LONG;
        } else if (typeDefinition.represents(float.class)) {
            return FLOAT;
        } else if (typeDefinition.represents(double.class)) {
            return DOUBLE;
        } else {
            throw new IllegalArgumentException("Not valid type for boxing: " + typeDefinition);
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor visitor, Context context) {
        visitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            wrapperType.getInternalName(),
            boxingMethodName,
            boxingMethodDescriptor,
            false
        );
        return size;
    }
}
