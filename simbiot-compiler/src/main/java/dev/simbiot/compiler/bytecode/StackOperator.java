package dev.simbiot.compiler.bytecode;

import java.lang.reflect.Method;
import java.util.function.Function;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

/**
 * Implements number operations for each number type.
 *
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public enum StackOperator {
    INTEGER(Operator.ForInteger::valueOf, RuntimeMethod.ForInteger::valueOf),
    DOUBLE(Operator.ForDouble::valueOf, RuntimeMethod.ForDouble::valueOf);

    private final Function<String, StackManipulation> operatorProvider;
    private final Function<String, StackManipulation> runtimeMethodProvider;

    <T extends Number> StackOperator(Function<String, StackManipulation> operatorProvider,
                                     Function<String, StackManipulation> runtimeMethodProvider) {
        this.operatorProvider = operatorProvider;
        this.runtimeMethodProvider = runtimeMethodProvider;
    }

    public static <T extends Number> StackOperator forType(TypeDefinition type) {
        return StackOperator.valueOf(type.getTypeName().toUpperCase());
    }

//    public StackManipulation add(StackManipulation left, StackManipulation right) {
//        return new Compound(left, right, getOperator("ADD"));
//    }
//
//    public StackManipulation sub(StackManipulation left, StackManipulation right) {
//        return new Compound(left, right, getOperator("SUB"));
//    }
//
//    public StackManipulation mul(StackManipulation left, StackManipulation right) {
//        return new Compound(left, right, getOperator("MUL"));
//    }
//
//    public StackManipulation div(StackManipulation left, StackManipulation right) {
//        return new Compound(left, right, getOperator("DIV"));
//    }
//
//    public StackManipulation pow(StackManipulation left, StackManipulation right) {
//        return new Compound(left, right, runtimeMethodProvider.apply("POW"));
//    }
//
//    public StackManipulation neg(StackManipulation operand) {
//        return new Compound(operand, getOperator("NEG"));
//    }

    public StackManipulation getOperator(String operator) {
        return operatorProvider.apply(operator);
    }

    /**
     * Default implementations of {@link StackManipulation} methods for number operations.
     */
    protected interface Operator<T extends Number> extends StackManipulation {

        @Override
        default Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitInsn(getOpcode());
            int sizeImpact = getOperandSizeImpact();
            return new Size(sizeImpact, Math.max(0, sizeImpact));
        }

        @Override
        default boolean isValid() {
            return true;
        }

        /**
         * Returns the instruction opcode to emit.
         *
         * @return the {@link Opcodes opcode}
         */
        int getOpcode();

        /**
         * Returns the size impact of this number operation. Note that the size change depends on the {@link StackSize}
         * of the operands involved.
         *
         * @return the size change of the stack after executing the instruction
         */
        default int getOperandSizeImpact() {
            return getOperandStackImpact() * StackSize.of(getOperandType()).getSize();
        }

        /**
         * Returns the impact of the operation represented by the number of operands pushed or popped, regardless the
         * {@link StackSize} of the operands.
         *
         * @return the change of the stack in number of operands after executing the instruction
         */
        int getOperandStackImpact();

        /**
         * Returns the type of the operands.
         *
         * @return the type of the operands
         */
        Class<T> getOperandType();

        /**
         * Stack stubs of instructions for math operation on longs.
         */
        enum ForInteger implements Operator<Integer> {
            ADD(Opcodes.IADD, -1),
            SUBTRACT(Opcodes.ISUB, -1),
            MULTIPLY(Opcodes.IMUL, -1),
            DIVIDE(Opcodes.IDIV, -1),
            NEG(Opcodes.INEG, 0);

            private final int opcode;
            private final int operandStackImpact;

            ForInteger(int opcode, int operandStackImpact) {
                this.opcode = opcode;
                this.operandStackImpact = operandStackImpact;
            }

            @Override
            public Class<Integer> getOperandType() {
                return int.class;
            }

            @Override
            public int getOperandStackImpact() {
                return operandStackImpact;
            }

            @Override
            public int getOpcode() {
                return opcode;
            }
        }

        /**
         * Stack stubs of instructions for math operation on doubles.
         */
        enum ForDouble implements Operator<Double> {
            ADD(Opcodes.DADD, -1),
            SUBTRACT(Opcodes.DSUB, -1),
            MULTIPLY(Opcodes.DMUL, -1),
            DIVIDE(Opcodes.DDIV, -1),
            NEG(Opcodes.DNEG, 0);

            private final int opcode;
            private final int operandStackImpact;

            ForDouble(int opcode, int operandStackImpact) {
                this.opcode = opcode;
                this.operandStackImpact = operandStackImpact;
            }

            @Override
            public Class<Double> getOperandType() {
                return double.class;
            }

            @Override
            public int getOperandStackImpact() {
                return operandStackImpact;
            }

            @Override
            public int getOpcode() {
                return opcode;
            }
        }
    }

    /**
     * Implementation of the number power operations.
     */
    protected interface RuntimeMethod<T extends Number> extends StackManipulation {

        @Override
        default Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return eval().apply(methodVisitor, implementationContext);
        }

        @Override
        default boolean isValid() {
            return true;
        }

        default StackManipulation eval() {
            Method method;
            try {
                method = Runtime.class.getMethod(getMethodName(), getOperandType(), getOperandType());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            return MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(method));
        }

        /**
         * Returns the instruction opcode to emit.
         *
         * @return the {@link Opcodes opcode}
         */
        String getMethodName();

        /**
         * Returns the type of the operands.
         *
         * @return the type of the operands
         */
        Class<T> getOperandType();

        /**
         * Stack stubs of instructions for math operation on longs.
         */
        enum ForInteger implements RuntimeMethod<Integer> {
            POW("powInt");

            private final String name;

            ForInteger(String name) {
                this.name = name;
            }

            @Override
            public String getMethodName() {
                return name;
            }

            @Override
            public Class<Integer> getOperandType() {
                return int.class;
            }
        }

        /**
         * Stack stubs of instructions for math operation on doubles.
         */
        enum ForDouble implements RuntimeMethod<Double> {
            POW("powDouble");

            private final String name;

            ForDouble(String name) {
                this.name = name;
            }

            @Override
            public String getMethodName() {
                return name;
            }

            @Override
            public Class<Double> getOperandType() {
                return double.class;
            }
        }
    }
}
