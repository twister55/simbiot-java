package dev.simbiot;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Runtime {

    public static boolean toBoolean(Object obj) {
        if (obj instanceof String) {
            return !"".equals(obj);
        }

        if (obj instanceof Integer) {
            return ((Integer) obj) != 0;
        }

        if (obj instanceof Boolean) {
            return Boolean.TRUE.equals(obj);
        }

        return obj != null;
    }

    public static String escape(String html) {
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
}
