package dev.simbiot.ast;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class ProgramLoader<T> {
    private final Class<T> type;
    private final ObjectMapper mapper;

    protected ProgramLoader(Class<T> type) {
        this.type = type;
        this.mapper = new ObjectMapper()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    protected abstract Program process(T value);

    public Program load(String id) throws IOException {
        return process(readValue(id));
    }

    protected T readValue(String id) throws IOException {
        return mapper.readValue(getStream(id), type);
    }

    protected InputStream getStream(String id) {
        return getClass().getClassLoader().getResourceAsStream(
            "ast/components/" + id.replace(".", "/") + ".json"
        );
    }
}
