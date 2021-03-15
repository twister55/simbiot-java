package dev.simbiot.ast.statement.declaration;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.expression.BaseClass;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.statement.ClassBody;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ClassDeclaration extends BaseClass implements Declaration {
    private final Identifier id;

    @JsonCreator
    public ClassDeclaration(@JsonProperty("id") Identifier id,
                            @JsonProperty("id") Expression superClass,
                            @JsonProperty("id") ClassBody body) {
        super("ClassDeclaration", superClass, body);
        this.id = id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable("when a class declaration is a part of the `export default class` statement")
    public Identifier getId() {
        return id;
    }
}
