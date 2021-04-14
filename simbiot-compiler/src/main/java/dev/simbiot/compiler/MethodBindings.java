package dev.simbiot.compiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import dev.simbiot.runtime.HTML;
import dev.simbiot.runtime.Objects;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class MethodBindings {
    private final Map<String, MethodDescription> mapping;

    public MethodBindings() {
        this.mapping = new HashMap<>();
        bindInternal(HTML.class, Objects.class);
    }

    public MethodDescription get(String name) {
        return mapping.get(name);
    }

    public void bind(Class<?>... types) {
        bind(types, false);
    }

    public void bindInternal(Class<?>... types) {
        bind(types, true);
    }

    private void bind(Class<?>[] types, boolean internal) {
        for (Class<?> type : types) {
            for (Method method : type.getMethods()) {
                if (Modifier.isStatic(method.getModifiers())) {
                    mapping.put(internal ? "@" + method.getName() : method.getName(), new ForLoadedMethod(method));
                }
            }
        }
    }
}
