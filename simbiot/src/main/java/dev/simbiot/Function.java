package dev.simbiot;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface Function {

    void apply(Map<String, Object> args) throws IOException;
}
