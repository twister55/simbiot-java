package dev.simbiot.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.ProgramLoader;
import dev.simbiot.ast.SourceType;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.ast.statement.StatementVisitor;
import dev.simbiot.ast.statement.declaration.Declaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration;
import dev.simbiot.ast.statement.declaration.VariableDeclaration.Kind;
import dev.simbiot.ast.statement.declaration.VariableDeclarator;
import dev.simbiot.ast.statement.module.ExportNamedDeclaration;
import dev.simbiot.ast.statement.module.ImportDeclaration;
import dev.simbiot.ast.statement.module.ImportDefaultSpecifier;
import dev.simbiot.ast.statement.module.ModuleSpecifier;
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
        final List<Statement> result = new ArrayList<>();
        final Map<String, String> urls = new HashMap<>();

        if (ast.instance != null) {
            process(ast.instance, result, urls);
        }

        if (ast.html != null) {
            String hash = ast.css != null ? ast.css.getHash() : null;
            process(ast.html, result, urls, hash);
        }

        return new Program(SourceType.SCRIPT, result);
    }

    private void process(Script instance, List<Statement> target, Map<String, String> urls) {
        instance.getContent().accept(new StatementVisitor() {
            @Override
            public void visit(ImportDeclaration statement) {
                final String source = statement.getSource().getString();

                if (source.endsWith(".svelte")) {
                    // ./nested/Inner.svelte -> nested.Inner
                    final String componentId = source
                        .substring(2, source.length() - 7)
                        .replace("/", ".");

                    for (ModuleSpecifier specifier : statement.getSpecifiers()) {
                        if (specifier instanceof ImportDefaultSpecifier) {
                            ImportDefaultSpecifier importDefault = (ImportDefaultSpecifier) specifier;
                            urls.put(importDefault.getLocal().getName(), componentId);
                        }
                    }
                }
            }

            @Override
            public void visit(ExportNamedDeclaration statement) {
                final Declaration declaration = statement.getDeclaration();

                if (declaration instanceof VariableDeclaration) {
                    target.add(prop((VariableDeclaration) declaration));
                }
            }

            @Override
            public void visit(VariableDeclaration statement) {
                target.add(statement);
            }
        });
    }

    private void process(Fragment fragment, List<Statement> target, Map<String, String> urls, @Nullable String hash) {
        final SvelteNodeVisitor visitor = new SvelteNodeVisitor(target, urls, hash);
        visitor.accept(fragment);
    }

    private Statement prop(VariableDeclaration declaration) {
        final List<VariableDeclarator> result = new ArrayList<>();

        for (final VariableDeclarator declarator : declaration.getDeclarations()) {
            final Identifier id = declarator.getId();
            final Literal name = new Literal(id.getName());

            if (declarator.getInit() != null) {
                result.add(new VariableDeclarator(id, new CallExpression("@attr", name, declarator.getInit())));
            } else {
                result.add(new VariableDeclarator(id, new CallExpression("@attr", name)));
            }
        }

        return new VariableDeclaration(Kind.LET, result);
    }
}
