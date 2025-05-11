package com.chyzman.namer.mixin.common;

import com.chyzman.namer.cca.component.NickStorage;
import com.chyzman.namer.pond.PlayerManagerDuck;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements PlayerManagerDuck {

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow @Final private List<ServerPlayerEntity> players;

    @Override
    public @Nullable ServerPlayerEntity namer$getPlayerByNick(String nick) {
        var scoreboard = server.getScoreboard();
        if (scoreboard == null) return null;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return null;

        for (ServerPlayerEntity player : players) {
            var playerNick = storage.getNick(player);
            if (playerNick != null && NickStorage.raw(playerNick).equals(nick)) return player;
        }
        return null;
    }


}
