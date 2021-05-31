package dev.simbiot.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.simbiot.Component;
import dev.simbiot.ComponentProvider;
import dev.simbiot.HTML;
import dev.simbiot.Runtime;
import dev.simbiot.ast.Program;
import dev.simbiot.ast.ProgramLoader;
import dev.simbiot.compiler.expression.ArrayExpressionHandler;
import dev.simbiot.compiler.expression.ArrowFunctionExpressionHandler;
import dev.simbiot.compiler.expression.BinaryExpressionHandler;
import dev.simbiot.compiler.expression.CallExpressionHandler;
import dev.simbiot.compiler.expression.ConditionalExpressionHandler;
import dev.simbiot.compiler.expression.ExpressionResolver;
import dev.simbiot.compiler.expression.FunctionExpressionHandler;
import dev.simbiot.compiler.expression.IdentifierHandler;
import dev.simbiot.compiler.expression.LiteralHandler;
import dev.simbiot.compiler.expression.LogicalExpressionHandler;
import dev.simbiot.compiler.expression.MemberExpressionHandler;
import dev.simbiot.compiler.expression.ObjectExpressionHandler;
import dev.simbiot.compiler.expression.UnaryExpressionHandler;
import dev.simbiot.compiler.expression.UpdateExpressionHandler;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CompilingProvider implements ComponentProvider {
    private final ProgramLoader<?> loader;
    private final ComponentCompiler compiler;
    private final Map<String, Component> cache;

    public CompilingProvider(ProgramLoader<?> loader) {
        this.loader = loader;
        this.compiler = new ComponentCompiler();
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public Component getComponent(String id) throws IOException {
        try {
            Component component = this.cache.get(id);

            if (component == null) {
                component = createComponent(id);
                this.cache.put(id, component);
            }

            return component;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    protected Component createComponent(String id) {
        final CompilerContext context = new CompilerContext(id, createMethodBindings(), createExpressionResolver());
        try {
            return createInstance(loadClass(context), getDependencies(context));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected MethodBindings createMethodBindings() {
        final MethodBindings bindings = new MethodBindings();
        bindings.bindInternal(HTML.class, Runtime.class);
        return bindings;
    }

    protected ExpressionResolver createExpressionResolver() {
        return new ExpressionResolver()
            .withHandler("ArrayExpression", new ArrayExpressionHandler())
            .withHandler("ArrowFunctionExpression", new ArrowFunctionExpressionHandler())
            .withHandler("BinaryExpression", new BinaryExpressionHandler())
            .withHandler("CallExpression", new CallExpressionHandler())
            .withHandler("ConditionalExpression", new ConditionalExpressionHandler())
            .withHandler("FunctionExpression", new FunctionExpressionHandler())
            .withHandler("Identifier", new IdentifierHandler())
            .withHandler("Literal", new LiteralHandler())
            .withHandler("LogicalExpression", new LogicalExpressionHandler())
            .withHandler("MemberExpression", new MemberExpressionHandler())
            .withHandler("ObjectExpression", new ObjectExpressionHandler())
            .withHandler("UnaryExpression", new UnaryExpressionHandler())
            .withHandler("UpdateExpression", new UpdateExpressionHandler());
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

    private Component[] getDependencies(CompilerContext context) throws IOException {
        final List<String> componentIds = context.getComponentIds();
        final Component[] components = new Component[componentIds.size()];
        for (int i = 0; i < componentIds.size(); i++) {
            components[i] = getComponent(componentIds.get(i));
        }
        return components;
    }

    private Class<? extends Component> loadClass(CompilerContext context) throws IOException {
        final Unloaded<Component> unloaded = compileAndSave(context, loader.load(context.getId()));
        final ClassLoader classLoader = Component.class.getClassLoader();

        if (context.getInlineTypes().isEmpty()) {
            return unloaded
                .load(classLoader, ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        } else {
            final Class<? extends Component> loaded = unloaded
                .load(classLoader, ClassLoadingStrategy.Default.CHILD_FIRST.opened())
                .getLoaded();

            for (Unloaded<?> inlineType : context.getInlineTypes()) {
                inlineType.load((InjectionClassLoader) loaded.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);
            }

            return loaded;
        }
    }

    private Unloaded<Component> compileAndSave(CompilerContext context, Program program) throws IOException {
        final Unloaded<Component> unloaded = compile(context, program);
        unloaded.saveIn(new File("generated"));
        for (Unloaded<?> inlineType : context.getInlineTypes()) {
            inlineType.saveIn(new File("generated"));
        }
        return unloaded;
    }

    private Unloaded<Component> compile(CompilerContext context, Program program) {
        return compiler.compile(context, program);
    }
}
