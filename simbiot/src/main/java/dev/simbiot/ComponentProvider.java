package dev.simbiot;

import java.io.IOException;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface ComponentProvider {

    Component getComponent(String id) throws IOException;
}
