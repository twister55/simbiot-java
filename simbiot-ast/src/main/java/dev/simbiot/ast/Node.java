package dev.simbiot.ast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Node {

    String getType();
}
