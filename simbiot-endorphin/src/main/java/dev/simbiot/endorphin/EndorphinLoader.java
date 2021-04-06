package dev.simbiot.endorphin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;

import dev.simbiot.ast.Program;
import dev.simbiot.ast.ProgramLoader;
import dev.simbiot.ast.SourceType;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.statement.Statement;
import dev.simbiot.endorphin.node.expression.IdentifierNode;
import dev.simbiot.endorphin.node.expression.IdentifierNode.Context;
import dev.simbiot.endorphin.node.ENDTemplate;
import dev.simbiot.endorphin.node.expression.ENDCaller;
import dev.simbiot.endorphin.node.expression.ENDFilter;
import dev.simbiot.endorphin.node.expression.ENDGetter;
import dev.simbiot.endorphin.node.expression.ENDGetterPrefix;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class EndorphinLoader extends ProgramLoader<EndorphinAst> {

    public EndorphinLoader() {
        super(EndorphinAst.class);
        registerModule(new SimpleModule()
            .registerSubtypes(
                IdentifierNode.class, ENDGetter.class, ENDCaller.class, ENDGetterPrefix.class, ENDFilter.class
            )
            .setDeserializerModifier(new BeanDeserializerModifier() {
                @Override
                public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                    if (beanDesc.getBeanClass() == Identifier.class) {
                        return new IdentifierDeserializer(deserializer);
                    }

                    return deserializer;
                }
            })
        );
    }

    @Override
    protected Program process(String id, EndorphinAst ast) {
        final List<Statement> result = new ArrayList<>();
        final Map<String, String> urls = new HashMap<>();
        final EndorphinNodeVisitor visitor = new EndorphinNodeVisitor(id, result, urls);

        visitor.accept((ENDTemplate) ast.getBody()[0]);

        return new Program(SourceType.SCRIPT, result);
    }

    public static class IdentifierDeserializer extends StdDeserializer<Identifier> implements ResolvableDeserializer {
        private final JsonDeserializer<?> defaultDeserializer;

        public IdentifierDeserializer(JsonDeserializer<?> defaultDeserializer) {
            super(Identifier.class);
            this.defaultDeserializer = defaultDeserializer;
        }

        @Override
        public Identifier deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final TreeNode treeNode = jp.readValueAsTree();
            final TextNode name = (TextNode) treeNode.path("name");
            final TreeNode context = treeNode.path("context");

            if (context.isMissingNode()) {
                return new Identifier(name.textValue());
            }

            return new IdentifierNode(name.textValue(), Context.parse(((TextNode) context).textValue()));
        }

        @Override
        public void resolve(DeserializationContext ctxt) throws JsonMappingException {
            ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
        }
    }
}
