package dev.simbiot.endorphin;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.Node;
import dev.simbiot.endorphin.node.ENDNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinAst {
    @Nullable
    public final String hash;
    public final List<ENDNode> body;

    @JsonCreator
    public EndorphinAst(@Nullable @JsonProperty("filename") String filename,
                        @JsonProperty("body") List<ENDNode> body) {
        this.hash = hash(filename);
        this.body = body;
        this.body.sort(Comparator.comparing(Node::getType));
    }

    // A simple function for calculation of has (Adler32) from given string
    private String hash(String filePath) {
        int s1 = 1, s2 = 0;
        for (int i = 0, len = filePath.length(); i < len; i++) {
            s1 = (s1 + filePath.charAt(i)) % 65521;
            s2 = (s2 + s1) % 65521;
        }
        return "e" + Integer.toString((s2 << 16) + s1, 36);
    }

}
