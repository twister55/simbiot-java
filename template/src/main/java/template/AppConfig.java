package template;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.simbiot.SimbiotViewResolver;

import dev.simbiot.parser.SvelteParser;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@Configuration
public class AppConfig {

    @Bean
    public ViewResolver getViewResolver(ResourceLoader loader) {
        SimbiotViewResolver resolver = new SimbiotViewResolver();
        resolver.setParser(new SvelteParser());
        resolver.setResourceLoader(loader);
        resolver.setCache(false);
        return resolver;
    }
}
