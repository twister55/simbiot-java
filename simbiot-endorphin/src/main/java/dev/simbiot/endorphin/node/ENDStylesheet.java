package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDStylesheet extends BaseNode {
    private final String mime;
    private final String content;
    private final String url;

    @JsonCreator
    protected ENDStylesheet(@JsonProperty("mime") String mime,
                            @JsonProperty("content") String content,
                            @JsonProperty("url") String url) {
        super("ENDStylesheet");
        this.mime = mime;
        this.content = content;
        this.url = url;
    }

    public String getMime() {
        return mime;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
