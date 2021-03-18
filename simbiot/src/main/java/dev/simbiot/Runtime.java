package dev.simbiot;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Runtime {

    @SuppressWarnings("rawtypes")
    public static Object access(Object obj, Object prop) {
        if (obj instanceof Map) {
            return ((Map) obj).get(prop);
        }

        if (obj instanceof List) {
            return ((List) obj).get(toInt(prop));
        }

        if (obj.getClass().isArray()) {
            return ((Object[]) obj)[toInt(prop)];
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

    public static int toInt(Object value) {
        return value instanceof Integer ? (Integer) value : Integer.parseInt(value.toString());
    }

    public static boolean toBoolean(Object value) {
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

    public static String escape(Object value) {
        if (value instanceof String) {
            String html = (String) value;
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < html.length(); i++) {
                char c = html.charAt(i);

                switch (c) {
                    case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;
                    case '&':
                        result.append("&amp;");
                        break;
                    case '"':
                        result.append("&quot;");
                        break;
                    case '\'':
                        result.append("&#39;");
                        break;
                    default:
                        result.append(c);
                }
            }

            return result.toString();
        }

        return String.valueOf(value);
    }
}
