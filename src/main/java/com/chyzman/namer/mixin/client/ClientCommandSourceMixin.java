package com.chyzman.namer.mixin.client;


import com.chyzman.namer.cca.component.NickStorage;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ClientCommandSource.class)
public abstract class ClientCommandSourceMixin {
    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "getPlayerNames", at = @At(value = "RETURN"), cancellable = true)
    private void addNicksToSuggestions(CallbackInfoReturnable<Collection<String>> cir, @Local List<String> list) {
        var storage = NICK_STORAGE.getNullable(networkHandler.getWorld().getScoreboard());
        if (storage == null) return;

        var uuids = networkHandler.getPlayerList().stream().map(playerListEntry -> playerListEntry.getProfile().getId()).toList();

        storage.allNicks().forEach((uuid, nick) -> {
            if (uuids.contains(uuid)) list.add(NickStorage.formatForCommand(nick));
        });
        cir.setReturnValue(list);
    }
}
