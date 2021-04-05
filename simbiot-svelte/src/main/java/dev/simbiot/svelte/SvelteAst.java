package dev.simbiot.svelte;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.svelte.template.Fragment;
import dev.simbiot.svelte.template.Script;
import dev.simbiot.svelte.template.Style;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SvelteAst {
    @Nullable
    public final Fragment html;
    @Nullable
    public final Script instance;
    @Nullable
    public final Script module;
    @Nullable
    public final Style css;

    @JsonCreator
    public SvelteAst(@Nullable @JsonProperty("html") Fragment html,
                     @Nullable @JsonProperty("instance") Script instance,
                     @Nullable @JsonProperty("module") Script module,
                     @Nullable @JsonProperty("css") Style css) {
        this.html = html;
        this.instance = instance;
        this.module = module;
        this.css = css;
    }
}
