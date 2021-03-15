package dev.simbiot.ast;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.Statement.Visitor;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Program extends BaseNode {
    private final SourceType sourceType;
    private final Statement[] body;
    private final Comment[] comments;

    public Program(Program program) {
        this(program.sourceType, program.body, program.comments);
    }

    public Program(SourceType sourceType, List<Statement> body) {
        this(sourceType, body.toArray(new Statement[0]));
    }

    public Program(SourceType sourceType, Statement... body) {
        this(sourceType, body, null);
    }

    @JsonCreator
    public Program(@JsonProperty("sourceType") SourceType sourceType,
                   @JsonProperty("body") Statement[] body,
                   @JsonProperty("comments") Comment[] comments) {
        super("Program");
        this.sourceType = sourceType;
        this.body = body;
        this.comments = comments;
    }

    public void accept(Visitor visitor) {
        for (Statement statement : body) {
            statement.accept(visitor);
        }
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public Statement[] getBody() {
        return body;
    }

    @Nullable
    public Comment[] getComments() {
        return comments;
    }
}
