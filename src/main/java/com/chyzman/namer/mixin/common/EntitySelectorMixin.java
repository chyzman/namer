package com.chyzman.namer.mixin.common;

import com.chyzman.namer.pond.EntitySelectorDuck;
import com.chyzman.namer.pond.PlayerManagerDuck;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorMixin implements EntitySelectorDuck {

    @Shadow @Final private @Nullable String playerName;

    //region DUCK

    @Unique private boolean playerNameIsNick = false;

    @Override
    public boolean namer$playerNameIsNick() {
        return playerNameIsNick;
    }

    @Override
    public EntitySelector namer$playerNameIsNick(boolean isNick) {
        this.playerNameIsNick = isNick;
        return (EntitySelector) (Object) this;
    }

    //endregion

    @ModifyExpressionValue(method = "getEntities(Lnet/minecraft/server/command/ServerCommandSource;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;getPlayer(Ljava/lang/String;)Lnet/minecraft/server/network/ServerPlayerEntity;"))
    public ServerPlayerEntity findViaNick$getEntities(ServerPlayerEntity original, @Local(argsOnly = true) ServerCommandSource source) {
        return checkForNick(source, original);
    }

    @ModifyExpressionValue(method = "getPlayers(Lnet/minecraft/server/command/ServerCommandSource;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;getPlayer(Ljava/lang/String;)Lnet/minecraft/server/network/ServerPlayerEntity;"))
    public ServerPlayerEntity findViaNick$getPlayers(ServerPlayerEntity original, @Local(argsOnly = true) ServerCommandSource source) {
        return checkForNick(source, original);
    }

    @Unique
    private ServerPlayerEntity checkForNick(ServerCommandSource source, ServerPlayerEntity fallback) {
        if (this.namer$playerNameIsNick()) return ((PlayerManagerDuck) source.getServer().getPlayerManager()).namer$getPlayerByNick(playerName);
        return fallback;
    }
}
