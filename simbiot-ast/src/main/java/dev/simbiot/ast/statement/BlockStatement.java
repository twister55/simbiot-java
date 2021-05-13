package dev.simbiot.ast.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import static java.util.Arrays.asList;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class BlockStatement extends BaseNode implements Statement, Iterable<Statement> {
    private final List<Statement> body;

    public BlockStatement(Statement... body) {
        this(asList(body));
    }

    @JsonCreator
    public BlockStatement(@JsonProperty("body") List<Statement> body) {
        this("BlockStatement", body);
    }

    protected BlockStatement(String type, List<Statement> body) {
        super(type);
        this.body = new ArrayList<>(body);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Iterator<Statement> iterator() {
        return body.iterator();
    }

    public BlockStatement prepend(Statement statement) {
        body.add(0, statement);
        return this;
    }

    public BlockStatement append(Statement statement) {
        body.add(statement);
        return this;
    }
}
