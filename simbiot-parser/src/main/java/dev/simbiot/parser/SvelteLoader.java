package dev.simbiot.parser;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.ProgramLoader;
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
import dev.simbiot.parser.template.Fragment;
import dev.simbiot.parser.template.Script;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SvelteLoader extends ProgramLoader<SvelteAst> {

    public SvelteLoader() {
        super(SvelteAst.class);
    }

    @Override
    protected Program process(SvelteAst ast) {
        final List<Statement> body = new ArrayList<>();

        process(ast.instance, body);
        process(ast.html, body);

        return new Program(SourceType.SCRIPT, body);
    }

    private void process(@Nullable Script instance, List<Statement> target) {
        if (instance != null) {
            for (Statement statement : instance.getContent().getBody()) {
                if (statement instanceof ExportNamedDeclaration) {
                    Declaration declaration = ((ExportNamedDeclaration) statement).getDeclaration();

                    if (declaration instanceof VariableDeclaration) {
                        target.add(prop((VariableDeclaration) declaration));
                    }
                }
            }
        }
    }

    private void process(@Nullable Fragment fragment, List<Statement> target) {
        if (fragment != null) {
            final SvelteNodeVisitor visitor = new SvelteNodeVisitor(target);
            visitor.accept(fragment);
        }
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
