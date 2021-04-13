package dev.simbiot.runtime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class HTML {
    private static final Set<String> SELF_CLOSING_TAGS = new HashSet<>(Arrays.asList("area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"));

    public static boolean isSelfClosing(String tagName) {
        return SELF_CLOSING_TAGS.contains(tagName) || tagName.toLowerCase().equals("!doctype");
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
