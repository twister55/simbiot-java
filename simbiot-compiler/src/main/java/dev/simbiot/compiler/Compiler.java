package dev.simbiot.compiler;

import java.io.File;

import dev.simbiot.Component;
import dev.simbiot.Props;
import dev.simbiot.Slots;
import dev.simbiot.Writer;
import dev.simbiot.ast.Program;
import dev.simbiot.compiler.bytecode.ArrayField;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.Opcodes;
import static dev.simbiot.compiler.bytecode.ArrayField.BYTE_ARRAY;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Compiler {
    public static final String CONSTANTS_FIELD_NAME = "$$CONSTANTS";

    private final ProgramProcessor processor = new ProgramProcessor();

    public Component compile(String id, Program program) {
        try {
            final Unloaded<Component> unloaded = process(id, program);

            unloaded.saveIn(new File("generated"));

            return unloaded
                .load(Compiler.class.getClassLoader())
                .getLoaded()
                .newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error while creating component", e);
        }
    }

    public Unloaded<Component> process(String id, Program program) {
        final ProcessContext context = new ProcessContext();

        return new ByteBuddy()
            .subclass(Component.class)
            .name("component." + id)
            .defineField(CONSTANTS_FIELD_NAME, BYTE_ARRAY, Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)
            .method(named("render"))
            .intercept(new Implementation() {
                @Override
                public ByteCodeAppender appender(Target target) {
                    return (mv, ctx, method) -> new ByteCodeAppender.Size(
                        process(context, program, ctx.getInstrumentedType()).apply(mv, ctx).getMaximalSize(),
                        method.getStackSize() + context.getLocalVarsCount()
                    );
                }

                @Override
                public InstrumentedType prepare(InstrumentedType type) {
                    return type;
                }
            })
            .initializer((visitor, ctx, method) -> {
                final StackManipulation.Size size = new StackManipulation.Compound(
                    new ArrayField(ctx.getInstrumentedType(), CONSTANTS_FIELD_NAME, context.getConstants())
                ).apply(visitor, ctx);

                return new ByteCodeAppender.Size(size.getMaximalSize(), method.getStackSize());
            })
            .make();
    }

    protected StackManipulation.Compound process(ProcessContext context, Program program, TypeDescription type) {
        prepareContext(context, type);
        return processor.process(context, program);
    }

    protected void prepareContext(ProcessContext context, TypeDescription type) {
        context.fields(type.getDeclaredFields());
        context.store("writer", Writer.class);
        context.store("props", Props.class);
        context.store("slots", Slots.class);
    }
}
