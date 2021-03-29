package dev.simbiot.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dev.simbiot.Component;
import dev.simbiot.ComponentProvider;
import dev.simbiot.ast.ProgramLoader;
import dev.simbiot.compiler.program.ProgramHandler;
import net.bytebuddy.dynamic.DynamicType.Unloaded;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CompilingProvider implements ComponentProvider {
    private final ProgramLoader<?> loader;
    private final Compiler compiler;

    public CompilingProvider(ProgramLoader<?> loader) {
        this.loader = loader;
        this.compiler = new Compiler(new ProgramHandler());
    }

    @Override
    public Component getComponent(String id) throws IOException {
        final CompilerContext context = new CompilerContext(id);
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

    private Component[] getDependencies(CompilerContext context) throws IOException {
        final List<String> componentIds = context.getComponentIds();
        final Component[] components = new Component[componentIds.size()];
        for (int i = 0; i < componentIds.size(); i++) {
            components[i] = getComponent(componentIds.get(i));
        }
        return components;
    }

    private Class<? extends Component> loadClass(CompilerContext context) throws IOException {
        return compileAndSave(context)
            .load(Component.class.getClassLoader())
            .getLoaded();
    }

    private Unloaded<Component> compileAndSave(CompilerContext context) throws IOException {
        final Unloaded<Component> unloaded = compile(context);
        unloaded.saveIn(new File("generated"));
        return unloaded;
    }

    private Unloaded<Component> compile(CompilerContext context) throws IOException {
        return compiler.compile(context, loader.load(context.getId()));
    }
}
