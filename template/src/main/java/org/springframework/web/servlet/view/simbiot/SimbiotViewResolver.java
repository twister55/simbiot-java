package org.springframework.web.servlet.view.simbiot;

import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import dev.simbiot.ComponentProvider;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SimbiotViewResolver extends AbstractTemplateViewResolver implements ViewResolver {
    private final ComponentProvider provider;

    public SimbiotViewResolver(ComponentProvider provider) {
        this.provider = provider;
        setViewClass(SimbiotView.class);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return SimbiotView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        final SimbiotView view = (SimbiotView) super.buildView(viewName);
        view.setViewComponent(provider.getComponent(viewName));
        return view;
    }
}
