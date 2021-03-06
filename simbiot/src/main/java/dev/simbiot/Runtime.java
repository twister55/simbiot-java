package dev.simbiot;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Runtime {

    public static boolean is(Object value) {
        if (value instanceof String) {
            return !"".equals(value);
        }

        if (value instanceof Integer) {
            return ((Integer) value) != 0;
        }

        if (value instanceof Boolean) {
            return Boolean.TRUE.equals(value);
        }

        return value != null;
    }

    public static String concat(Object... args) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }

    public static Map<String, Object> object(Object... args) {
        final Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                result.put(String.valueOf(args[i]), args[i + 1]);
            }
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public static Object access(Object obj, Object prop) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Map) {
            return ((Map) obj).get(prop);
        }

        if (obj instanceof List) {
            return ((List) obj).get((Integer) prop);
        }

        if (obj.getClass().isArray()) {
            return ((Object[]) obj)[(Integer) prop];
        }

        for (Field field : obj.getClass().getFields()) {
            if (field.getName().equals(prop)) {
                try {
                    return field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(prop + " can not be accessed in " + obj);
                }
            }
        }

        return null;
    }

    public static Object getter(Object... args) {
        Object value = args[0];

        for (int i = 1; i < args.length; i++) {
            value = access(value, args[i]);

            if (value == null) {
                break;
            }
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    public static Iterator<Object> iterator(Object obj) {
        if (obj instanceof Iterable) {
            return ((Iterable<Object>) obj).iterator();
        }

        if (obj.getClass().isArray()) {
            return Arrays.asList((Object[]) obj).iterator();
        }

        throw new IllegalArgumentException(obj + " is not iterable");
    }
}
