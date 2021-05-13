package org.springframework.web.servlet.view.simbiot;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractTemplateView;

import dev.simbiot.Component;
import dev.simbiot.Function;
import dev.simbiot.runtime.StreamWriter;
import dev.simbiot.runtime.Writer;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SimbiotView extends AbstractTemplateView {
    private Component component;
    private Component template;

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        applyContentType(response);
        render(model, response);
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public void setTemplate(Component template) {
        this.template = template;
    }

    private void render(Map<String, Object> model, HttpServletResponse response) throws Exception {
        final Writer writer = new StreamWriter(response.getOutputStream());
        final Map<String, Object> props = Collections.singletonMap(
            "slot:body", (Function) (args) -> component.render(writer, model)
        );

        this.template.render(writer, props);
    }
}
