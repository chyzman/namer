package com.chyzman.namer.mixin.common;

import com.chyzman.namer.pond.PlayerEntityDuck;
import com.chyzman.namer.util.NickFormatter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity {

    public ServerPlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void nicksInPlayerList(CallbackInfoReturnable<Text> cir) {
        var scoreboard = getWorld().getScoreboard();
        if (scoreboard == null) return;

        var storage = NICK_STORAGE.getNullable(scoreboard);
        if (storage == null) return;

        var nick = storage.getNick(getUuid());
        if (nick == null) return;

        cir.setReturnValue(NickFormatter.nickAndName(nick, ((PlayerEntityDuck)this).namer$getActualDisplayName()));
    }
}
