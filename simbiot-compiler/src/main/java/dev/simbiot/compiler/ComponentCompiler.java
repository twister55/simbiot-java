package dev.simbiot.compiler;

import dev.simbiot.Component;
import dev.simbiot.ast.Program;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import static dev.simbiot.compiler.Dispatcher.COMPONENTS_FIELD_NAME;
import static dev.simbiot.compiler.Dispatcher.CONSTANTS_FIELD_NAME;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ComponentCompiler extends Compiler {

    public DynamicType.Unloaded<Component> compile(CompilerContext ctx, Program program) {
        return compile(ctx, Component.class, program.getBody());
    }

    @Override
    protected <T> DynamicType.Builder<T> createBuilder(CompilerContext ctx, Class<T> type) {
        return super.createBuilder(ctx, type)
            .defineField(CONSTANTS_FIELD_NAME, of(byte[][].class), Visibility.PROTECTED, FieldManifestation.FINAL, Ownership.STATIC)
            .defineField(COMPONENTS_FIELD_NAME, of(Component[].class), Visibility.PROTECTED, FieldManifestation.FINAL)
            .initializer(initializer(ctx))
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(Component[].class)
            .intercept(FieldAccessor.ofField(COMPONENTS_FIELD_NAME).setsArgumentAt(0));
    }

    private ByteCodeAppender initializer(CompilerContext context) {
        return (visitor, ctx, method) -> {
            final StackManipulation.Size size = new StackManipulation.Compound(
                ArrayFactory.forType(of(byte[].class).asGenericType()).withValues(context.getConstants()),
                FieldAccess.forField(
                    ctx.getInstrumentedType()
                        .getDeclaredFields()
                        .filter(named(CONSTANTS_FIELD_NAME))
                        .getOnly()
                ).write()
            ).apply(visitor, ctx);

            return new ByteCodeAppender.Size(size.getMaximalSize(), method.getStackSize());
        };
    }
}
