package dev.simbiot.runtime;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class END {

    public static String componentStart(String name, String hash, Map<String, Object> props) {
        StringBuilder builder = new StringBuilder("<")
            .append(name)
            .append(" ")
            .append(hash)
            .append("-host");

        if (!props.isEmpty()) {
            for (Map.Entry<String, Object> entry : props.entrySet()) {
                final String propKey = entry.getKey();
                final Object propValue = entry.getValue();

                if (propKey.contains(":")) {
                    continue;
                }

                builder.append(" ").append(propKey);

                if (propValue != null) {
                    final Class<?> propValueClass = propValue.getClass();

                    if (propValue instanceof String || propValueClass.isPrimitive()) {
                        builder.append("=\"").append(propValue).append("\"");
                    } else if (propValue instanceof Collection || propValueClass.isArray()) {
                        builder.append("=\"[]\"");
                    } else {
                        builder.append("=\"{}\"");
                    }
                }
            }
        }

        return builder.append(">").toString();
    }

}
