package org.springframework.web.servlet.view.simbiot;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractTemplateView;

import dev.simbiot.Component;
import dev.simbiot.MapProps;
import dev.simbiot.Slots;
import dev.simbiot.StreamWriter;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SimbiotView extends AbstractTemplateView {
    private Component page;

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        applyContentType(response);
        render(model, response);
    }

    public void setViewComponent(Template template, Component body) {
        this.page = new Page(template, body);
    }

    private void render(Map<String, Object> model, HttpServletResponse response) throws Exception {
        final StreamWriter writer = new StreamWriter(response.getOutputStream());
        page.render(writer, new MapProps(model), new Slots());
        writer.flush();
    }
}
