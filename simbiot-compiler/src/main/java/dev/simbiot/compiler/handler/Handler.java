package dev.simbiot.compiler.handler;

import dev.simbiot.ast.Node;
import dev.simbiot.compiler.CompilerContext;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface Handler<N extends Node> {

    void handle(CompilerContext ctx, N node, HandleResult result);
}
