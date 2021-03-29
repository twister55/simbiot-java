package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.simbiot.compiler.bytecode.ConstantBytes;
import dev.simbiot.compiler.program.Chunk;
import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.REFERENCE;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CompilerContext {
    private final String id;
    private final Map<String, Integer> refs;
    private final Map<String, Generic> types;
    private final List<StackManipulation> constants;
    private final List<String> componentIds;

    private FieldList<InDefinedShape> fields;
    private ParameterList<?> parameters;
    private int offset;

    public CompilerContext(String id) {
        this.id = id;
        this.refs = new HashMap<>();
        this.types = new HashMap<>();
        this.constants = new ArrayList<>();
        this.componentIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getLocalVarsCount() {
        return refs.size();
    }

    public Chunk resolve(String name) {
        if ("undefined".equals(name)) {
            return Chunk.NULL;
        }

        final Integer offset = refs.get(name);
        if (offset == null) {
            final FieldList<InDefinedShape> filter = fields.filter(named(name));
            if (!filter.isEmpty()) {
                final InDefinedShape shape = filter.getOnly();
                final Compound manipulation = new Compound(
                    shape.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(),
                    FieldAccess.forField(shape).read()
                );
                return new Chunk(manipulation, shape.getType());
            }
            return Chunk.NULL;
        }

        return new Chunk(REFERENCE.loadFrom(offset), types.get(name));
    }

    public Chunk argument(int index) {
        final ParameterDescription o = this.parameters.get(index);
        return new Chunk(REFERENCE.loadFrom(o.getOffset()), o.getType());
    }

    public void fields(FieldList<InDefinedShape> fields) {
        this.fields = fields;
    }

    public void parameters(ParameterList<?> parameters) {
        this.parameters = parameters;
        this.offset = parameters.size();
    }

    public StackManipulation store(String name, Chunk result) {
        final int ref = ++offset;
        types.put(name, result.type());
        refs.put(name, ref);
        return new Compound(result.build(), REFERENCE.storeAt(ref));
    }

    public List<StackManipulation> getConstants() {
        return constants;
    }

    public int addConstant(String text) {
        final int index = constants.size();
        constants.add(new ConstantBytes(text));
        return index;
    }

    public List<String> getComponentIds() {
        return componentIds;
    }

    public int addComponentId(String id) {
        final int index = componentIds.size();
        componentIds.add(id);
        return index;
    }
}
