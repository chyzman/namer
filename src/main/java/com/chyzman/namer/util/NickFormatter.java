package com.chyzman.namer.util;

import com.chyzman.namer.Namer;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NickFormatter {
    private static final NodeParser NODE_PARSER = TagParser.QUICK_TEXT_WITH_STF_SAFE;

    public static Text parseNick(@Nullable String nick) {
        if (nick == null || nick.isBlank()) return Text.empty();
        if (!Namer.CONFIG.allowNickFormatting()) return Text.literal(nick);
        return NODE_PARSER.parseText(nick, ParserContext.of());
    }

    public static Text nickAndName(@Nullable Text nick, Text name) {
        if (nick == null) return name;
        return Text.empty().append(nick).append(" (").append(name).append(")");
    }
}
