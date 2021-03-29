package dev.simbiot;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface Component {

    interface Slot {
        Slot EMPTY = () -> {};

        void render() throws IOException;
    }

    void render(Writer writer, Map<String, Object> props, Map<String, Slot> slots) throws IOException;

}
