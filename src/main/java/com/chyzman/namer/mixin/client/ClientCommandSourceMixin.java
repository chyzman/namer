package com.chyzman.namer.mixin.client;


import com.chyzman.namer.cca.NickStorage;
import com.chyzman.namer.impl.NickSuggestion;
import com.chyzman.namer.pond.CommandSourceDuck;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
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
public abstract class ClientCommandSourceMixin implements CommandSourceDuck {
    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Override
    public List<NickSuggestion.Data> namer$getNickSuggestionData() {
        var storage = NICK_STORAGE.getNullable(networkHandler.getWorld().getScoreboard());
        return networkHandler.getPlayerList().stream()
            .map(playerListEntry -> {
                var profile = playerListEntry.getProfile();
                return new NickSuggestion.Data(
                    Text.literal(profile.getName()),
                    storage == null ? null : storage.getNick(profile.getId())
                );
            })
            .toList();
    }
}
