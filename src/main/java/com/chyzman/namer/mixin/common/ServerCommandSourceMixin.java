package com.chyzman.namer.mixin.common;


import com.chyzman.namer.impl.NickSuggestionData;
import com.chyzman.namer.pond.CommandSourceDuck;
import com.chyzman.namer.pond.PlayerEntityDuck;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerCommandSource.class)
public abstract class ServerCommandSourceMixin implements CommandSourceDuck {
    @Shadow @Final private MinecraftServer server;

    @Override
    public List<NickSuggestionData> namer$getNickSuggestionData() {
        return server.getPlayerManager().getPlayerList().stream()
            .map(serverPlayerEntity -> new NickSuggestionData(serverPlayerEntity.getName(), serverPlayerEntity.getDisplayName()))
            .toList();
    }
}
