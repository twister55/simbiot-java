package dev.simbiot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TemplateProvider implements ComponentProvider {

    @Override
    public Component getComponent(String id) {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(id);
        final String html = new BufferedReader(new InputStreamReader(stream))
            .lines()
            .map(String::trim)
            .collect(Collectors.joining(""));

        return new Template(html);
    }
}
