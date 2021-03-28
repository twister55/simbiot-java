package template;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.simbiot.SimbiotViewResolver;

import dev.simbiot.ComponentProvider;
import dev.simbiot.compiler.CompilingProvider;
import dev.simbiot.parser.SvelteLoader;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@Configuration
public class AppConfig {

    @Bean
    public ViewResolver getViewResolver(ComponentProvider provider) {
        SimbiotViewResolver resolver = new SimbiotViewResolver(provider);
        resolver.setCache(false);
        return resolver;
    }

    @Bean
    public ComponentProvider getComponentProvider() {
        return new CompilingProvider(new SvelteLoader());
    }
}
