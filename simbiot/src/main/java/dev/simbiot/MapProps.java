package dev.simbiot;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class MapProps implements Props {
    private final Map<String, Object> data;

    public MapProps() {
        this.data = new HashMap<>();
    }

    public MapProps(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> K get(String name) {
        return (K) data.get(name);
    }

    public <K> MapProps set(String name, K value) {
        data.put(name, value);
        return this;
    }
}
