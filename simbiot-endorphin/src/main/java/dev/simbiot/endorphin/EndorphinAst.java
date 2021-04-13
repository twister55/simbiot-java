package dev.simbiot.endorphin;

import java.util.Arrays;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.Node;
import dev.simbiot.endorphin.node.ENDNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinAst {
    public final String hash;
    public final ENDNode[] body;

    @JsonCreator
    public EndorphinAst(@JsonProperty("filename") String filename,
                        @JsonProperty("body") ENDNode[] body) {
        this.hash = hash(filename);
        this.body = body;
        Arrays.sort(body, Comparator.comparing(Node::getType));
    }

    /**
     * A simple function for calculation of has (Adler32) from given string
     * @param filename path to component file
     * @return hash for css scoping
     */
    private String hash(String filename) {
        int s1 = 1, s2 = 0;
        for (int i = 0, len = filename.length(); i < len; i++) {
            s1 = (s1 + filename.charAt(i)) % 65521;
            s2 = (s2 + s1) % 65521;
        }
        return "e" + Integer.toString((s2 << 16) + s1, 36);
    }
}
