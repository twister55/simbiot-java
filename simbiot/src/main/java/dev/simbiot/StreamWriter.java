package dev.simbiot;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class StreamWriter implements Writer {
    private final OutputStream stream;

    public StreamWriter(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void write(byte[] value) throws IOException {
        stream.write(value);
    }

    @Override
    public void write(Object value, boolean escape) throws IOException {
        if (value instanceof String) {
            String str = (String) value;

            write((escape ? Runtime.escape(str) : str).getBytes());
        } else {
            write(String.valueOf(value).getBytes());
        }
    }

    @Override
    public void flush() throws IOException {
        stream.flush();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
