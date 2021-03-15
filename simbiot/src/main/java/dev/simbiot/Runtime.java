package dev.simbiot;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Runtime {

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
