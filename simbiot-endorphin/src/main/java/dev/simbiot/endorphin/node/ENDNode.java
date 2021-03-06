package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
    @Type(value = ENDChooseStatement.class, name = "ENDChooseStatement"),
    @Type(value = ENDElement.class, name = "ENDElement"),
    @Type(value = ENDForEachStatement.class, name = "ENDForEachStatement"),
    @Type(value = ENDIfStatement.class, name = "ENDIfStatement"),
    @Type(value = ENDImport.class, name = "ENDImport"),
    @Type(value = ENDInnerHTML.class, name = "ENDInnerHTML"),
    @Type(value = ENDTemplate.class, name = "ENDTemplate"),
    @Type(value = ENDPartial.class, name = "ENDPartial"),
    @Type(value = ENDPartialStatement.class, name = "ENDPartialStatement"),
    @Type(value = ENDVariableStatement.class, name = "ENDVariableStatement"),
    @Type(value = ENDLiteral.class, name = "Literal"),
    @Type(value = ENDProgram.class, name = "Program"),
})
public interface ENDNode extends Node {

    void accept(Visitor visitor);

    interface Visitor {

        void visit(ENDImport node);

        void visit(ENDPartial node);

        void visit(ENDTemplate node);

        void visit(ENDProgram node);

        void visit(ENDLiteral node);

        void visit(ENDInnerHTML node);

        void visit(ENDElement node);

        void visit(ENDVariableStatement node);

        void visit(ENDIfStatement node);

        void visit(ENDChooseStatement node);

        void visit(ENDForEachStatement node);

        void visit(ENDPartialStatement node);
    }
}
