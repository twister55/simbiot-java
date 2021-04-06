package app.simbiot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.simbiot.SimbiotViewResolver;

import dev.simbiot.compiler.CompilingProvider;
import dev.simbiot.compiler.ComponentCompiler;
import dev.simbiot.endorphin.ENDExpressionsResolver;
import dev.simbiot.endorphin.EndorphinLoader;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@Configuration
public class AppConfig {

    @Bean
    public ViewResolver getViewResolver() {
        return new SimbiotViewResolver(
            new CompilingProvider(new EndorphinLoader(), new ComponentCompiler(new ENDExpressionsResolver()))
        );
    }

}
