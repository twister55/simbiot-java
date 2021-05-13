package dev.simbiot.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.simbiot.Component;
import dev.simbiot.Function;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.FunctionExpression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.pattern.AssignmentPattern;
import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration.Kind;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.runtime.Writer;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
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
    private static final Generic SCOPE_TYPE = of(Object[].class).asGenericType();
    private static final Generic COMPONENTS_TYPE = of(Component[].class).asGenericType();

    public Unloaded<Function> compile(CompilerContext ctx, FunctionExpression expression) {
        return compile(ctx, Function.class, getBody(expression.getBody(), expression.getParams()));
    }

    @Override
    protected <T> Builder<T> createBuilder(CompilerContext ctx, Class<T> type) {
        return super.createBuilder(ctx, type)
            .innerTypeOf(ctx.type())
            .asMemberType()
            .modifiers(Visibility.PRIVATE, TypeManifestation.FINAL, Ownership.STATIC)
            .defineField("writer", WRITER_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField("props", PROPS_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField("scope", SCOPE_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineField("components", COMPONENTS_TYPE, Visibility.PRIVATE, FieldManifestation.FINAL)
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(WRITER_TYPE, PROPS_TYPE, SCOPE_TYPE, COMPONENTS_TYPE)
            .intercept(
                FieldAccessor.ofField("writer").setsArgumentAt(0)
                    .andThen(FieldAccessor.ofField("props").setsArgumentAt(1))
                    .andThen(FieldAccessor.ofField("scope").setsArgumentAt(2))
                    .andThen(FieldAccessor.ofField("components").setsArgumentAt(3))
            );
    }

    private BlockStatement getBody(BlockStatement body, Pattern[] params) {
        final List<VariableDeclarator> declarators = new ArrayList<>();

        for (Pattern pattern : params) {
            if (pattern instanceof AssignmentPattern) {
                final AssignmentPattern assign = (AssignmentPattern) pattern;
                final Identifier name = (Identifier) assign.getLeft();
                final CallExpression value = new CallExpression(
                    BuiltIn.ARG_GET_OR_DEFAULT, new Literal(name.getName()), assign.getRight()
                );

                declarators.add(new VariableDeclarator(name, value));
            }
        }

        return body.prepend(new VariableDeclaration(Kind.CONST, declarators));
    }
}
