package dev.simbiot.endorphin;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.endorphin.node.TemplateNode;
import dev.simbiot.endorphin.node.Script;
import dev.simbiot.endorphin.node.ENDStylesheet;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinAst {
    @Nullable
    private final String filename;
    private final TemplateNode[] body;
    private final ENDStylesheet[] stylesheets;
    private final Script[] scripts;
    /** List of local variables (JS-safe) used in partial. Added by template optimizer */
    private final String[] variables;

    @JsonCreator
    public EndorphinAst(@Nullable @JsonProperty("filename") String filename,
                        @JsonProperty("body") TemplateNode[] body,
                        @JsonProperty("stylesheets") ENDStylesheet[] stylesheets,
                        @JsonProperty("scripts") Script[] scripts,
                        @JsonProperty("variables") String[] variables) {
        this.filename = filename;
        this.body = body;
        this.stylesheets = stylesheets;
        this.scripts = scripts;
        this.variables = variables;
    }

    public String getFilename() {
        return filename;
    }

    public TemplateNode[] getBody() {
        return body;
    }

    public ENDStylesheet[] getStylesheets() {
        return stylesheets;
    }

    public Script[] getScripts() {
        return scripts;
    }

    public String[] getVariables() {
        return variables;
    }
}
