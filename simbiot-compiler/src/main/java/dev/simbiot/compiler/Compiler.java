package dev.simbiot.compiler;

import dev.simbiot.Component;
import dev.simbiot.ast.Program;
import dev.simbiot.compiler.bytecode.ArrayField;
import dev.simbiot.compiler.program.ProgramHandler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import static dev.simbiot.compiler.program.Dispatcher.COMPONENTS_FIELD_NAME;
import static dev.simbiot.compiler.program.Dispatcher.CONSTANTS_FIELD_NAME;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Compiler {
    private final ProgramHandler handler;

    public Compiler(ProgramHandler handler) {
        this.handler = handler;
    }

    public Unloaded<Component> compile(CompilerContext context, Program program) {
        return new ByteBuddy()
            .subclass(Component.class)
            .name(context.getId())
            .defineField(CONSTANTS_FIELD_NAME, of(byte[][].class), Visibility.PRIVATE, FieldManifestation.FINAL, Ownership.STATIC)
            .defineField(COMPONENTS_FIELD_NAME, of(Component[].class), Visibility.PRIVATE, FieldManifestation.PLAIN)
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(Component[].class)
            .intercept(FieldAccessor.ofField(COMPONENTS_FIELD_NAME).setsArgumentAt(0))
            .method(named("render"))
            .intercept(renderMethod(context, program))
            .initializer(staticInitializer(context))
            .make();
    }

    private ByteCodeAppender staticInitializer(CompilerContext context) {
        return (visitor, ctx, method) -> {
            final StackManipulation.Size size = new ArrayField(
                ctx.getInstrumentedType(), CONSTANTS_FIELD_NAME, context.getConstants()
            ).apply(visitor, ctx);

            return new ByteCodeAppender.Size(size.getMaximalSize(), method.getStackSize());
        };
    }

    private Implementation renderMethod(CompilerContext context, Program program) {
        return new Implementation() {
            @Override
            public ByteCodeAppender appender(Target target) {
                return (mv, ctx, method) -> {
                    context.fields(ctx.getInstrumentedType().getDeclaredFields());
                    context.parameters(method.getParameters());

                    return new ByteCodeAppender.Size(
                        handler.handle(context, program).apply(mv, ctx).getMaximalSize(),
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
