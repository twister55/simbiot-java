package dev.simbiot.runtime;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface Writer extends Closeable {

    void write(byte[] value) throws IOException;

    void write(Object value) throws IOException;
}
