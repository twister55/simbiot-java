package dev.simbiot.ast;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UnsupportedNodeException extends RuntimeException {

    public UnsupportedNodeException(Node node, String place) {
        super(node.getType() + " is not supported " + place);
    }

}
