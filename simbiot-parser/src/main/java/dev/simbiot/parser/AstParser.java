package dev.simbiot.parser;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.simbiot.ast.Program;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class AstParser<T> {
    private final Class<T> type;
    private final ObjectMapper mapper;

    protected AstParser(Class<T> type) {
        this.type = type;
        this.mapper = new ObjectMapper()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public abstract Program parse(InputStream in) throws IOException;

    protected T readValue(InputStream in) throws IOException {
        return mapper.readValue(in, type);
    }
}
