package dev.simbiot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Slots {
    private final Map<String, Slot> mapping;

    public Slots() {
        this.mapping = new HashMap<>();
    }

    public Slots add(Slot slot) {
        mapping.put(null, slot);
        return this;
    }

    public Slots add(String name, Slot slot) {
        mapping.put(name, slot);
        return this;
    }

    public void render() throws IOException {
        mapping.getOrDefault(null, Slot.EMPTY).render();
    }

    public void render(Slot defaultValue) throws IOException {
        mapping.getOrDefault(null, defaultValue).render();
    }

    public void render(String name) throws IOException {
        mapping.getOrDefault(name, Slot.EMPTY).render();
    }

    public void render(String name, Slot defaultValue) throws IOException {
        mapping.getOrDefault(name, defaultValue).render();
    }

    public interface Slot {
        Slot EMPTY = () -> {};

        void render() throws IOException;
    }
}
