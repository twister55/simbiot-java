package dev.simbiot;

import java.io.IOException;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface Component {

    void render(Writer writer, Props props, Slots slots) throws IOException;

}
