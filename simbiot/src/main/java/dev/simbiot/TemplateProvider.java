package dev.simbiot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TemplateProvider implements ComponentProvider {

    @Override
    public Component getComponent(String id) throws IOException {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(id);

        if (stream == null) {
            throw new IOException("Template ('" + id + "') not found");
        }

        final String html = new BufferedReader(new InputStreamReader(stream))
            .lines()
            .map(String::trim)
            .collect(Collectors.joining(""));

        return new Template(html);
    }
}
