package dev.simbiot.parser;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.parser.template.Fragment;
import dev.simbiot.parser.template.Script;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Ast {
    public final Fragment html;
    @Nullable
    public final Script instance;
    @Nullable
    public final Script module;

    @JsonCreator
    public Ast(@JsonProperty("html") Fragment html,
               @Nullable @JsonProperty("instance") Script instance,
               @Nullable @JsonProperty("module") Script module) {
        this.html = html;
        this.instance = instance;
        this.module = module;
    }
}
