package org.springframework.web.servlet.view.simbiot;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import dev.simbiot.Component;
import dev.simbiot.ast.Program;
import dev.simbiot.compiler.Compiler;
import dev.simbiot.parser.AstParser;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SimbiotViewResolver extends AbstractTemplateViewResolver implements ViewResolver, ResourceLoaderAware {
    private ResourceLoader loader;
    private AstParser<?> parser;
    private Compiler compiler;

    public SimbiotViewResolver() {
        setPrefix("classpath:ast/components/");
        setSuffix(".json");
        setViewClass(SimbiotView.class);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return SimbiotView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        final SimbiotView view = (SimbiotView) super.buildView(viewName);
        view.setViewComponent(readTemplate(), createComponent(viewName, view.getUrl()));
        return view;
    }

    @Override
    public void setResourceLoader(ResourceLoader loader) {
        this.loader = loader;
    }

    public void setParser(AstParser<?> parser) {
        this.parser = parser;
    }

    public void setCompiler(Compiler compiler) {
        this.compiler = compiler;
    }

    private Component createComponent(String name, String url) throws IOException {
        final Program program = parser.parse(getInputStream(url));

        if (compiler == null) {
            compiler = new Compiler();
        }

        return compiler.compile(name, program);
    }

    private Template readTemplate() throws IOException {
        try (final TemplateReader reader = new TemplateReader(getInputStream("classpath:template.html"))) {
            return reader.read();
        }
    }

    private InputStream getInputStream(String resourceName) throws IOException {
        Resource resource = loader.getResource(resourceName);
        if (resource.exists()) {
            return resource.getInputStream();
        }
        throw new IOException("No template exists named: " + resourceName);
    }
}
