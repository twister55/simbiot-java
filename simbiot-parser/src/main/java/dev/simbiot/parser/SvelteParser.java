package dev.simbiot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.SourceType;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.StatementVisitor;
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
            ast.instance.getContent().accept(new StatementVisitor() {
                @Override
                public void visit(ExportNamedDeclaration declaration) {
                    final Declaration exportedDeclaration = declaration.getDeclaration();

                    if (exportedDeclaration instanceof VariableDeclaration) {
                        result.add(createPropDeclaration((VariableDeclaration) exportedDeclaration));
                    }
                }
            });
        }

        accept(ast.html, result);

        return new Program(SourceType.SCRIPT, result);
    }

    private Statement createPropDeclaration(VariableDeclaration declaration) {
        final VariableDeclarator[] declarations = declaration.getDeclarations();
        final VariableDeclarator[] result = new VariableDeclarator[declarations.length];

        for (int i = 0; i < declarations.length; i++) {
            final VariableDeclarator declarator = declarations[i];
            result[i] = new VariableDeclarator(
                declarator.getId(),
                new CallExpression("get", new Literal(declarator.getId().getName()), declarator.getInit())
            );
        }

        return new VariableDeclaration(Kind.LET, result);
    }
}
