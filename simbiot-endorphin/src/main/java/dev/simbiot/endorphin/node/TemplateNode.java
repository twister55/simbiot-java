package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ENDChooseStatement.class, name = "ENDChooseStatement"),
    @Type(value = ENDElement.class, name = "ENDElement"),
    @Type(value = ENDForEachStatement.class, name = "ENDForEachStatement"),
    @Type(value = ENDIfStatement.class, name = "ENDIfStatement"),
    @Type(value = ENDInnerHTML.class, name = "ENDInnerHTML"),
    @Type(value = ENDTemplate.class, name = "ENDTemplate"),
    @Type(value = ENDVariableStatement.class, name = "ENDVariableStatement"),
    @Type(value = ENDLiteral.class, name = "Literal"),
    @Type(value = ENDProgram.class, name = "Program"),
})
public interface TemplateNode extends dev.simbiot.ast.Node {

    void accept(Visitor visitor);

    interface Visitor {

        void visit(ENDTemplate template);

        void visit(ENDProgram program);

        void visit(ENDLiteral literal);

        void visit(ENDInnerHTML innerHTML);

        void visit(ENDElement element);

        void visit(ENDVariableStatement statement);

        void visit(ENDIfStatement statement);

        void visit(ENDChooseStatement statement);

        void visit(ENDForEachStatement statement);
    }
}
