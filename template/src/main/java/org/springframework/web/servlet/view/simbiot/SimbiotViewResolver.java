package org.springframework.web.servlet.view.simbiot;

import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import dev.simbiot.ComponentProvider;
import dev.simbiot.TemplateProvider;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SimbiotViewResolver extends AbstractTemplateViewResolver implements ViewResolver {
    private final ComponentProvider componentProvider;
    private final TemplateProvider templateProvider;

    public SimbiotViewResolver(ComponentProvider provider) {
        this(new TemplateProvider(), provider);
    }

    public SimbiotViewResolver(TemplateProvider templateProvider, ComponentProvider componentProvider) {
        this.templateProvider = templateProvider;
        this.componentProvider = componentProvider;
        setViewClass(SimbiotView.class);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return SimbiotView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        final SimbiotView view = (SimbiotView) super.buildView(viewName);
        view.setComponent(componentProvider.getComponent(viewName));
        view.setTemplate(templateProvider.getComponent("template.html"));
        return view;
    }
}
