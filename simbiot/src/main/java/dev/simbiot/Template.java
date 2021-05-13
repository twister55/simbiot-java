package dev.simbiot;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
class Template implements Component {
    private static final Pattern PATTERN = Pattern.compile("\\$\\{body\\}", Pattern.DOTALL);
    private final String[] parts;

    Template(String html) {
        this.parts = PATTERN.split(html);
    }

    @Override
    public void render(Writer writer, Map<String, Object> props) throws IOException {
        writer.write(parts[0]);
        ((Function) props.get("slot:body")).apply(Collections.emptyMap());
        writer.write(parts[1]);
    }
}
