package dev.simbiot.compiler;

import java.util.Map;

import dev.simbiot.Component;
import dev.simbiot.Component.Slot;
import dev.simbiot.ast.expression.ArrowFunctionExpression;
import dev.simbiot.runtime.Writer;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.FieldAccessor;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.parameterizedType;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class FunctionCompiler extends Compiler {
    private static final Generic WRITER_TYPE = of(Writer.class).asGenericType();
    private static final Generic PROPS_TYPE = parameterizedType(Map.class, String.class, Object.class).build();
    private static final Generic SLOTS_TYPE = parameterizedType(Map.class, String.class, Slot.class).build();
    private static final Generic SCOPE_TYPE = of(Object[].class).asGenericType();
    private static final Generic COMPONENTS_TYPE = of(Component[].class).asGenericType();

    public FunctionCompiler(ExpressionResolver resolver) {
        super(resolver);
    }

    public TypeDescription compile(CompilerContext ctx, ArrowFunctionExpression expression) {
        final Unloaded<Slot> unloaded = compile(ctx, Slot.class, expression.getBody());
        ctx.addInlineType(unloaded);
        return unloaded.getTypeDescription();
    }

    @Override
    protected <T> Builder<T> createBuilder(CompilerContext ctx, Class<T> type) {
        return super.createBuilder(ctx, type)
            .innerTypeOf(ctx.type())
            .asMemberType()
            .defineField("writer", WRITER_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField("props", PROPS_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField("slots", SLOTS_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField("vars", SCOPE_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField("components", COMPONENTS_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(WRITER_TYPE, PROPS_TYPE, SLOTS_TYPE, SCOPE_TYPE, COMPONENTS_TYPE)
            .intercept(
                FieldAccessor.ofField("writer").setsArgumentAt(0)
                    .andThen(FieldAccessor.ofField("props").setsArgumentAt(1))
                    .andThen(FieldAccessor.ofField("slots").setsArgumentAt(2))
                    .andThen(FieldAccessor.ofField("vars").setsArgumentAt(3))
                    .andThen(FieldAccessor.ofField("components").setsArgumentAt(4))
            );
    }
}
