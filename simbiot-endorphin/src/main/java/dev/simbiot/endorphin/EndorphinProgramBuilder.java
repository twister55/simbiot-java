package dev.simbiot.endorphin;

import java.util.List;

import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.pattern.Property;
import dev.simbiot.compiler.ProgramBuilder;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinProgramBuilder extends ProgramBuilder {
    private final String hash;

    public EndorphinProgramBuilder(String hash) {
        this.hash = hash;
    }

    @Override
    public void writeAttributes(List<Property> attributes) {
        attributes.add(0, new Property(new Identifier(hash), Literal.NULL));
        super.writeAttributes(attributes);
    }
}
