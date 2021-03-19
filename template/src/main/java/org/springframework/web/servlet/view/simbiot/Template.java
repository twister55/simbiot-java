package org.springframework.web.servlet.view.simbiot;

import java.io.IOException;
import java.util.List;

import dev.simbiot.Component;
import dev.simbiot.Props;
import dev.simbiot.Slots;
import dev.simbiot.Writer;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Template implements Component {
    private final List<String> parts;

    public Template(List<String> parts) {
        this.parts = parts;
    }

    @Override
    public void render(Writer writer, Props props, Slots slots) throws IOException {
        for (int i = 0; i < parts.size(); i++) {
            if (i % 2 == 0) {
                writer.write(parts.get(i));
            } else {
                slots.render(parts.get(i));
            }
        }
    }
}
