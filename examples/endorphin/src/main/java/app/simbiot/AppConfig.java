package app.simbiot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.simbiot.SimbiotViewResolver;

import dev.simbiot.endorphin.EndorphinProvider;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@Configuration
public class AppConfig {

    @Bean
    public ViewResolver getViewResolver() {
        return new SimbiotViewResolver(new EndorphinProvider());
    }

}
