package dev.simbiot.compiler.bytecode;

import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;

/**
 * Adds a jump target label. Used to indicate the destination of a jump. Used at the end of an
 * if-statement and beginning and end of loops.
 *
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class JumpTarget implements StackManipulation {
    private final Label label;

    public JumpTarget(Label label) {
        this.label = label;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor mv, Context ctx) {
        mv.visitLabel(label);
        return new Size(0, 0);
    }
}
