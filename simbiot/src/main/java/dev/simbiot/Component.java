package dev.simbiot;

import java.io.IOException;
import java.util.Map;

import dev.simbiot.runtime.Writer;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface Component {

    void render(Writer writer, Map<String, Object> props) throws IOException;
}
