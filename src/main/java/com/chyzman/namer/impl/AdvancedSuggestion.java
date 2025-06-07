package com.chyzman.namer.impl;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AdvancedSuggestion extends Suggestion {
    private final String completion;
    private final List<String> aliases;

    public AdvancedSuggestion(
        StringRange range,
        String display,
        String completion,
        List<String> aliases,
        @Nullable Message tooltip
    ) {
        super(range, display, tooltip);
        this.completion = completion;
        this.aliases = aliases;
    }

    public AdvancedSuggestion(StringRange range, String display, String completion, List<String> aliases) {
        this(range, display, completion, aliases, null);
    }


    public String getCompletion() {
        return completion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvancedSuggestion that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(completion, that.completion) && Objects.equals(aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), completion, aliases);
    }

    @Override
    public int compareTo(Suggestion o) {
        if (o instanceof AdvancedSuggestion advanced) {
            return aliases.stream()
                .map(alias -> advanced.aliases.stream()
                    .map(alias::compareTo)
                    .sorted()
                    .findFirst()
                    .orElse(0))
                .sorted()
                .findFirst()
                .orElse(0);
        }
        return aliases.stream()
            .map(alias -> alias.compareTo(o.getText()))
            .sorted()
            .findFirst()
            .orElse(0);
    }

    @Override
    public int compareToIgnoreCase(Suggestion b) {
        if (b instanceof AdvancedSuggestion advanced) {
            return aliases.stream()
                .map(alias -> advanced.aliases.stream()
                    .map(alias::compareToIgnoreCase)
                    .sorted()
                    .findFirst()
                    .orElse(0))
                .sorted()
                .findFirst()
                .orElse(0);
        }
        return aliases.stream()
            .map(alias -> alias.compareToIgnoreCase(b.getText()))
            .sorted()
            .findFirst()
            .orElse(0);
    }
}
