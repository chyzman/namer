package com.chyzman.namer.impl;

import com.chyzman.namer.util.NickFormatter;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NickSuggestion extends Suggestion {
    private final Data data;

    public NickSuggestion(StringRange range, Data data, Message tooltip) {
        super(range, data.nickAndName(), tooltip);
        this.data = data;
    }

    public NickSuggestion(StringRange range, Data data) {
        this(range, data, null);
    }

    @Override
    public String getText() {
        return data.nickAndName();
    }

    public String getCompletion() {
        return data.nameString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NickSuggestion that = (NickSuggestion) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }

    @Override
    public int compareTo(Suggestion o) {
        return Math.max(data.nameString().compareTo(o.getText()), data.nickString().compareTo(o.getText()));
    }

    @Override
    public int compareToIgnoreCase(Suggestion b) {
        return Math.max(data.nameString().compareToIgnoreCase(b.getText()), data.nickString().compareToIgnoreCase(b.getText()));
    }

    public record Data(Text name, @Nullable Text nick) {
        public String nickAndName() {
            return NickFormatter.nickAndName(nick, name).getString();
        }

        public String nameString() {
            return name.getString();
        }

        public String nickString() {
            return nick == null ? name.getString() : nick.getString();
        }
    }
}
