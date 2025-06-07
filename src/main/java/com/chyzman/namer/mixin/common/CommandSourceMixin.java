package com.chyzman.namer.mixin.common;


import com.chyzman.namer.impl.NickSuggestionData;
import com.chyzman.namer.pond.CommandSourceDuck;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(CommandSource.class)
public interface CommandSourceMixin extends CommandSourceDuck {
    @Shadow Collection<String> getPlayerNames();

    @Override
    default List<NickSuggestionData> namer$getNickSuggestionData() {
        return getPlayerNames().stream()
            .map(playerName -> new NickSuggestionData(Text.literal(playerName), null))
            .toList();
    }
}
