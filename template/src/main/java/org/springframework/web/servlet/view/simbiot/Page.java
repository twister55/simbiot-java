package org.springframework.web.servlet.view.simbiot;

import java.io.IOException;

import dev.simbiot.Component;
import dev.simbiot.Props;
import dev.simbiot.Slots;
import dev.simbiot.Writer;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Page implements Component {
    private final Template template;
    private final Component component;

    public Page(Template template, Component component) {
        this.template = template;
        this.component = component;
    }

    @Override
    public void render(Writer writer, Props props, Slots slots) throws IOException {
//        slots.add("body", () -> component.render(writer, props, slots));
//        slots.add("head", () -> head.render(writer, props, slots));

        component.render(writer, props, slots);
    }
}
