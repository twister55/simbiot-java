package dev.simbiot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.SourceType;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.declaration.Declaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration.Kind;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.ast.statement.module.ExportNamedDeclaration;
import static dev.simbiot.parser.TemplateNodeVisitor.accept;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SvelteParser extends AstParser<Ast> {

    public SvelteParser() {
        super(Ast.class);
    }

    @Override
    public Program parse(InputStream in) throws IOException {
        final List<Statement> result = new ArrayList<>();
        final Ast ast = readValue(in);

        if (ast.instance != null) {
            for (Statement statement : ast.instance.getContent().getBody()) {
                if (statement instanceof ExportNamedDeclaration) {
                    Declaration declaration = ((ExportNamedDeclaration) statement).getDeclaration();

                    if (declaration instanceof VariableDeclaration) {
                        result.add(prop((VariableDeclaration) declaration));
                    }
                }
            }
        }

        accept(ast.html, result);

        return new Program(SourceType.SCRIPT, result);
    }

    private Statement prop(VariableDeclaration declaration) {
        final List<VariableDeclarator> result = new ArrayList<>();

        for (final VariableDeclarator declarator : declaration.getDeclarations()) {
            final Identifier id = declarator.getId();
            final Literal name = new Literal(id.getName());

            if (declarator.getInit() != null) {
                result.add(new VariableDeclarator(id, new CallExpression("attr", name, declarator.getInit())));
            } else {
                result.add(new VariableDeclarator(id, new CallExpression("attr", name)));
            }
        }

        return new VariableDeclaration(Kind.LET, result);
    }
}
