package org.springframework.web.servlet.view.simbiot;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class TemplateReader implements Closeable {
    private static final Pattern PATTERN = Pattern.compile("(.+?)\\$\\{(\\w*?)\\}(.+?)", Pattern.DOTALL);
    private final Reader reader;

    public TemplateReader(InputStream stream) {
        this.reader = new InputStreamReader(stream);
    }

    public Template read() {
        return new Template(parseParts());
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private List<String> parseParts() {
        final List<String> result = new ArrayList<>();
        final Matcher matcher = PATTERN.matcher(readString());
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    private String readString() {
        return new BufferedReader(reader)
                .lines()
                .collect(Collectors.joining(""));
    }
}
