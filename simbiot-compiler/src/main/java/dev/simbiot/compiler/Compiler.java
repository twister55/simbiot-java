package dev.simbiot.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dev.simbiot.Component;
import dev.simbiot.ComponentProvider;
import dev.simbiot.Props;
import dev.simbiot.Slots;
import dev.simbiot.Writer;
import dev.simbiot.ast.Program;
import dev.simbiot.ast.ProgramLoader;
import dev.simbiot.compiler.bytecode.ArrayField;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.Opcodes;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Compiler implements ComponentProvider {
    public static final String CONSTANTS_FIELD_NAME = "$$CONSTANTS";
    public static final String COMPONENTS_FIELD_NAME = "$$components";

    private final ProgramLoader<?> loader;
    private final ProgramProcessor processor;

    public Compiler(ProgramLoader<?> loader) {
        this.loader = loader;
        this.processor = new ProgramProcessor();
    }

    @Override
    public Component getComponent(String id) throws IOException {
        final ProcessContext context = new ProcessContext(id);

        return createInstance(loadClass(context), getDependencies(context));
    }

    private Component createInstance(Class<? extends Component> type, Component[] dependencies) {
        try {
            return type
                .getConstructor(Component[].class)
                .newInstance((Object) dependencies);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Component[] getDependencies(ProcessContext context) throws IOException {
        final List<String> componentIds = context.getInlineComponentIds();
        final Component[] components = new Component[componentIds.size()];
        for (int i = 0; i < componentIds.size(); i++) {
            components[i] = getComponent(componentIds.get(i));
        }
        return components;
    }

    private Class<? extends Component> loadClass(ProcessContext context) throws IOException {
        return compileAndSave(context)
            .load(Component.class.getClassLoader())
            .getLoaded();
    }

    private Unloaded<Component> compileAndSave(ProcessContext context) throws IOException {
        final Unloaded<Component> unloaded = compile(context);
        unloaded.saveIn(new File("generated"));
        return unloaded;
    }

    private Unloaded<Component> compile(ProcessContext context) throws IOException {
        return new ByteBuddy()
            .subclass(Component.class)
            .name(context.getId())
            .defineField(CONSTANTS_FIELD_NAME, of(byte[][].class), Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)
            .defineField(COMPONENTS_FIELD_NAME, of(Component[].class), Visibility.PRIVATE, FieldManifestation.PLAIN)
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(Component[].class)
            .intercept(FieldAccessor.ofField(COMPONENTS_FIELD_NAME).setsArgumentAt(0))
            .method(named("render"))
            .intercept(renderMethod(context, loader.load(context.getId())))
            .initializer(staticInitializer(context))
            .make();
    }

    private ByteCodeAppender staticInitializer(ProcessContext context) {
        return (visitor, ctx, method) -> {
            final StackManipulation.Size size = new ArrayField(
                ctx.getInstrumentedType(), CONSTANTS_FIELD_NAME, context.getConstants()
            ).apply(visitor, ctx);

            return new ByteCodeAppender.Size(size.getMaximalSize(), method.getStackSize());
        };
    }

    private Implementation renderMethod(ProcessContext context, Program program) {
        return new Implementation() {
            @Override
            public ByteCodeAppender appender(Target target) {
                return (mv, ctx, method) -> {
                    context.fields(ctx.getInstrumentedType().getDeclaredFields());
                    context.store("writer", Writer.class);
                    context.store("props", Props.class);
                    context.store("slots", Slots.class);

                    return new ByteCodeAppender.Size(
                        processor.process(context, program).apply(mv, ctx).getMaximalSize(),
                        method.getStackSize() + context.getLocalVarsCount()
                    );
                };
            }

            @Override
            public InstrumentedType prepare(InstrumentedType type) {
                return type;
            }
        };
    }
}
