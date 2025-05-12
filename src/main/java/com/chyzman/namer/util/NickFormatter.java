package com.chyzman.namer.util;

import net.minecraft.text.Text;

public class NickFormatter {
    public static Text nickAndName(Text nick, Text name) {
        if (nick == null) return name;
        return nick.copy().append(" (").append(name).append(")");
    }
}
