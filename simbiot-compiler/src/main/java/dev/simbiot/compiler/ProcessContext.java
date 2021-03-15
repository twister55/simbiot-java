package dev.simbiot.compiler;

import java.util.HashMap;
import java.util.Map;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import static net.bytebuddy.implementation.bytecode.member.FieldAccess.forField;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.REFERENCE;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ProcessContext {
    private final static String[] ARG_NAMES = new String[] { "writer", "props", "slots" };

    private final FieldList<FieldDescription.InDefinedShape> fields;
    private final Map<String, Integer> refs;
    private final Map<String, TypeDescription.Generic> types;

    private int offset;

    public ProcessContext(Context context, MethodDescription method) {
        this.fields = context.getInstrumentedType().getDeclaredFields();
        this.refs = new HashMap<>();
        this.types = new HashMap<>();

        for (int i = 0; i < ARG_NAMES.length; i++) {
            this.storeRef(ARG_NAMES[i], method.getParameters().get(i).getType());
        }
    }

    public StackManipulation store(String name) {
        return REFERENCE.storeAt(storeRef(name));
    }

    public StackManipulation get(String name) {
        if ("undefined".equals(name)) {
            return NullConstant.INSTANCE;
        }

        return getRef(name);
    }

    public TypeDescription.Generic getType(String name) {
        return types.computeIfAbsent(name, this::getFieldType);
    }

    private StackManipulation getRef(String name) {
        final Integer offset = refs.get(name);
        return offset != null ? REFERENCE.loadFrom(offset) : getField(name);
    }

    private void storeRef(String name, TypeDescription.Generic type) {
        this.storeRef(name);
        this.types.put(name, type);
    }

    private int storeRef(String name) {
        final int ref = ++offset;
        this.refs.put(name, ref);
        return ref;
    }

    private StackManipulation getField(String name) {
        final FieldList<InDefinedShape> filter = fields.filter(named(name));
        return filter.isEmpty() ? NullConstant.INSTANCE : forField(filter.getOnly()).read();
    }

    private TypeDescription.Generic getFieldType(String name) {
        return fields.filter(named(name)).getOnly().getType();
    }
}
