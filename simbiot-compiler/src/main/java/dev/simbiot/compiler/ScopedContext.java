package dev.simbiot.compiler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import static net.bytebuddy.description.type.TypeDescription.Generic.OfNonGenericType.ForLoadedType.of;
import static net.bytebuddy.implementation.bytecode.collection.ArrayFactory.forType;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ScopedContext extends CompilerContext {
    private final Map<String, Chunk> vars;

    public ScopedContext(CompilerContext ctx, int index) {
        super(ctx.getId() + "$Fn$" + index, ctx);
        this.vars = new LinkedHashMap<>();
    }

    public StackManipulation vars(Function<String, Chunk> mapper) {
        final List<StackManipulation> values = vars.keySet().stream()
            .map(mapper)
            .map(Chunk::result)
            .collect(Collectors.toList());

        return forType(TypeDescription.ForLoadedType.of(Object.class).asGenericType()).withValues(values);
    }

    @Override
    public Chunk writer() {
        return field("writer");
    }

    @Override
    public Chunk props() {
        return field("props");
    }

    @Override
    public Chunk slots() {
        return field("slots");
    }

    @Override
    protected Chunk ref(String name) {
        final Chunk ref = super.ref(name);

        if (ref != Chunk.NULL) {
            return ref;
        }

        return vars.computeIfAbsent(name, key -> {
            final int index = vars.size();
            return field("vars")
                .append(IntegerConstant.forValue(index))
                .append(ArrayAccess.REFERENCE.load(), of(Object.class));
        });
    }
}
