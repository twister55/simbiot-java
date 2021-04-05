package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import dev.simbiot.ast.Node;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@JsonSubTypes(value = {
        @Type(value = Attribute.class, name = "Attribute"),
        @Type(value = AwaitBlock.class, name = "AwaitBlock"),
        @Type(value = CatchBlock.class, name = "CatchBlock"),
        @Type(value = Comment.class, name = "Comment"),
        @Type(value = DebugTag.class, name = "DebugTag"),
        @Type(value = EachBlock.class, name = "EachBlock"),
        @Type(value = Element.class, name = "Element"),
        @Type(value = ElseBlock.class, name = "ElseBlock"),
        @Type(value = EventHandler.class, name = "EventHandler"),
        @Type(value = Fragment.class, name = "Fragment"),
        @Type(value = Head.class, name = "Head"),
        @Type(value = IfBlock.class, name = "IfBlock"),
        @Type(value = InlineComponent.class, name = "InlineComponent"),
        @Type(value = KeyBlock.class, name = "KeyBlock"),
        @Type(value = MustacheTag.class, name = "MustacheTag"),
        @Type(value = PendingBlock.class, name = "PendingBlock"),
        @Type(value = RawMustacheTag.class, name = "RawMustacheTag"),
        @Type(value = Script.class, name = "Script"),
        @Type(value = Slot.class, name = "Slot"),
        @Type(value = Style.class, name = "Style"),
        @Type(value = Text.class, name = "Text"),
        @Type(value = ThenBlock.class, name = "ThenBlock"),
        @Type(value = Title.class, name = "Title")
})
public interface TemplateNode extends Node {

    void accept(Visitor visitor);

    interface Visitor {

        void visit(AwaitBlock block);

        void visit(Comment comment);

        void visit(DebugTag debugTag);

        void visit(EachBlock block);

        void visit(Element element);

        void visit(Head head);

        void visit(IfBlock block);

        void visit(InlineComponent component);

        void visit(KeyBlock block);

        void visit(MustacheTag tag);

        void visit(RawMustacheTag tag);

        void visit(Slot slot);

        void visit(Text text);

        void visit(Title title);
    }
}
