package dev.simbiot.compiler;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import dev.simbiot.Component;
import dev.simbiot.ast.Program;
import dev.simbiot.ast.statement.Statement;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.Opcodes;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType.of;
import static net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import static net.bytebuddy.implementation.bytecode.StackManipulation.Size;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Compiler {
    private static final String STATIC_PARTS_FIELD_NAME = "$$PARTS";
    private static final int STATIC_PARTS_FIELD_MODIFIERS = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
    private static final TypeDescription.Generic BYTE_ARRAY_TYPE = of(byte[][].class).asGenericType();
    private static final ArrayFactory ARRAY_FACTORY = ArrayFactory.forType(of(byte[].class).asGenericType());
    private static final MethodDescription GET_BYTES;

    static {
        try {
            GET_BYTES = new MethodDescription.ForLoadedMethod(String.class.getMethod("getBytes"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Component compile(String id, Program program) {
        final ProgramTransformer transformer = new ProgramTransformer();

        transformer.transform(program);

        return compile(id, transformer.statements(), transformer.parts());
    }

    public Component compile(String id, List<Statement> statements, List<String> parts) {
        try {
            final Unloaded<Component> unloaded = new ByteBuddy()
                    .subclass(Component.class)
                    .name("component." + id)
                    .initializer(initializer(parts))
                    .defineField(STATIC_PARTS_FIELD_NAME, BYTE_ARRAY_TYPE, STATIC_PARTS_FIELD_MODIFIERS)
                    .method(named("render"))
                    .intercept(renderMethod(statements))
                    .make();

            unloaded.saveIn(new File("generated"));

            return unloaded
                    .load(Compiler.class.getClassLoader())
                    .getLoaded()
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error while creating component", e);
        }
    }

    private ByteCodeAppender initializer(List<String> parts) {
        return (visitor, ctx, method) -> {
            final FieldDescription field = ctx.getInstrumentedType()
                    .getDeclaredFields()
                    .filter(named(STATIC_PARTS_FIELD_NAME))
                    .getOnly();

            final List<Compound> staticParts = parts.stream()
                    .map(TextConstant::new)
                    .map(constant -> new Compound(constant, MethodInvocation.invoke(GET_BYTES)))
                    .collect(Collectors.toList());

            final Size size = new Compound(
                    ARRAY_FACTORY.withValues(staticParts),
                    FieldAccess.forField(field).write()
            ).apply(visitor, ctx);

            return new ByteCodeAppender.Size(size.getMaximalSize(), method.getStackSize());
        };
    }

    private Implementation renderMethod(List<Statement> statements) {
        return new Implementation() {
            @Override
            public ByteCodeAppender appender(Target target) {
                return new ProgramAppender(statements);
            }

            @Override
            public InstrumentedType prepare(InstrumentedType type) {
                return type;
            }
        };
    }
}
