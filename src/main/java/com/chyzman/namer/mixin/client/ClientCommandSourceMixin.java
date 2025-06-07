package com.chyzman.namer.mixin.client;


import com.chyzman.namer.cca.NickStorage;
import com.chyzman.namer.impl.NickSuggestionData;
import com.chyzman.namer.pond.CommandSourceDuck;
import com.chyzman.namer.util.NickFormatter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(ClientCommandSource.class)
public abstract class ClientCommandSourceMixin implements CommandSourceDuck {
    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Override
    public List<NickSuggestionData> namer$getNickSuggestionData() {
        var storage = NICK_STORAGE.getNullable(networkHandler.getWorld().getScoreboard());
        return networkHandler.getPlayerList().stream()
            .map(playerListEntry -> {
                var profile = playerListEntry.getProfile();
                var nick = storage == null ? null : storage.getNick(profile.getId());
                return new NickSuggestionData(Text.literal(profile.getName()), nick);
            })
            .toList();
    }

    @WrapOperation(method = "getChatSuggestions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientCommandSource;getPlayerNames()Ljava/util/Collection;"))
    private Collection<String> namer$addNicksToChatSuggestions(ClientCommandSource instance, Operation<Collection<String>> original) {
        var originalResult = original.call(instance);
        var storage = NICK_STORAGE.getNullable(networkHandler.getWorld().getScoreboard());
        if (storage == null) return originalResult;
        originalResult.addAll(
            networkHandler.getPlayerList().stream()
                .map(playerListEntry -> storage.getNick(playerListEntry.getProfile().getId()))
                .filter(Objects::nonNull)
                .map(Text::getString)
                .toList()
        );
        return originalResult.stream().distinct().toList();
    }
}
