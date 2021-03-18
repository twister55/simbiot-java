package dev.simbiot.compiler.bytecode;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ObjectAccess extends Compound {
    public static final MethodDescription METHOD;

    static {
        try {
            METHOD = new ForLoadedMethod(Runtime.class.getMethod("access", Object.class, Object.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectAccess(Expression expression) {
        super(key(expression), MethodInvocation.invoke(METHOD));
    }

    private static StackManipulation key(Expression expression) {
        if (expression instanceof Identifier) {
            return new TextConstant(((Identifier) expression).getName());
        }

        if (expression instanceof Literal) {
            return new Constant((Literal) expression);
        }

        throw new RuntimeException(expression.getType() + " is not supported in object accessor");
    }
}
