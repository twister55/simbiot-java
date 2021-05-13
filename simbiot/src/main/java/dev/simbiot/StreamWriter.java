package dev.simbiot;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class StreamWriter implements Writer, Closeable {
    private final OutputStream stream;

    public StreamWriter(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void write(byte[] value) throws IOException {
        stream.write(value);
    }

    @Override
    public void write(Object value) throws IOException {
        write(String.valueOf(value).getBytes());
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
