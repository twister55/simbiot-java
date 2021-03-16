package dev.simbiot.compiler.bytecode;

import java.util.List;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ArrayField extends StackManipulation.Compound {
    public static final TypeDescription.Generic BYTE_ARRAY = of(byte[].class).asGenericType();

    public ArrayField(TypeDescription type, String name, List<StackManipulation> values) {
        super(
            ArrayFactory.forType(BYTE_ARRAY).withValues(values),
            FieldAccess.forField(
                type.getDeclaredFields().filter(named(name)).getOnly()
            ).write()
        );
    }
}
