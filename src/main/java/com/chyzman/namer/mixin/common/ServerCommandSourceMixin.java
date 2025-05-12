package com.chyzman.namer.mixin.common;


import com.chyzman.namer.impl.NickSuggestion;
import com.chyzman.namer.pond.CommandSourceDuck;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ServerCommandSource.class)
public abstract class ServerCommandSourceMixin implements CommandSourceDuck {
    @Shadow @Final private MinecraftServer server;

    @Override
    public List<NickSuggestion.Data> namer$getNickSuggestionData() {
        var storage = NICK_STORAGE.getNullable(server.getScoreboard());
        return server.getPlayerManager().getPlayerList().stream()
            .map(serverPlayerEntity ->
                     new NickSuggestion.Data(
                         serverPlayerEntity.getName(),
                         storage == null ? null : storage.getNick(serverPlayerEntity.getUuid())
                     ))
            .toList();
    }
}
