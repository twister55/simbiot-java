package dev.simbiot.compiler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import static dev.simbiot.compiler.bytecode.StackChunk.NULL;
import static net.bytebuddy.description.type.TypeDescription.Generic.OfNonGenericType.ForLoadedType.of;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class FunctionContext extends CompilerContext {
    private final Map<String, StackChunk> vars;

    public FunctionContext(CompilerContext ctx, int index) {
        super(ctx.getId() + "$Fn$" + index, ctx);
        this.vars = new LinkedHashMap<>();
    }

    public StackManipulation vars(Function<String, StackChunk> mapper) {
        final List<StackManipulation> vars = this.vars.keySet().stream().map(mapper).collect(Collectors.toList());
        return ArrayFactory.forType(ForLoadedType.of(Object.class).asGenericType()).withValues(vars);
    }

    @Override
    public StackChunk writer() {
        return field("writer");
    }

    @Override
    public StackChunk props() {
        return field("props");
    }

    @Override
    public StackChunk resolve(String name) {
        final StackChunk var = super.resolve(name);

        if (var != NULL) {
            return var;
        }

        return vars.computeIfAbsent(name, key -> {
            final int index = vars.size();
            return field("scope")
                .append(IntegerConstant.forValue(index))
                .append(ArrayAccess.REFERENCE.load(), of(Object.class));
        });
    }
}
