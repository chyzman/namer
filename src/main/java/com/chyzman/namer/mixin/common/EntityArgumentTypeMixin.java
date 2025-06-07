package com.chyzman.namer.mixin.common;

import com.chyzman.namer.impl.AdvancedSuggestion;
import com.chyzman.namer.impl.NickSuggestionData;
import com.chyzman.namer.pond.CommandSourceDuck;
import com.chyzman.namer.util.NickFormatter;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import static net.minecraft.command.CommandSource.shouldSuggest;

@Mixin(EntityArgumentType.class)
public abstract class EntityArgumentTypeMixin {

    @ModifyExpressionValue(method = "method_9311", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandSource;getPlayerNames()Ljava/util/Collection;"))
    private Collection<String> removeVanillaPlayerSuggestions(Collection<String> original) {
        return new ArrayList<>();
    }

    @Inject(method = "method_9311", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandSource;suggestMatching(Ljava/lang/Iterable;Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;)Ljava/util/concurrent/CompletableFuture;"))
    private void listNickSuggestions(
        CommandSource source,
        SuggestionsBuilder builder,
        CallbackInfo ci
    ) {
        var nickData = ((CommandSourceDuck) source).namer$getNickSuggestionData();
        if (nickData.isEmpty()) return;
        for (NickSuggestionData data : nickData) {
            var remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
            var aliases = new ArrayList<String>();
            aliases.add(data.name().getString().toLowerCase(Locale.ROOT));
            if (data.nick() != null) aliases.add(data.nick().getString().toLowerCase(Locale.ROOT));
            if (aliases.stream().anyMatch(string -> shouldSuggest(remaining, string))) {
                ((SuggestionsBuilderAccessor) builder).namer$getResult().add(
                    new AdvancedSuggestion(
                        StringRange.between(builder.getStart(), builder.getInput().length()),
                        NickFormatter.nickAndName(data.nick(), data.name()).getString(),
                        data.name().getString(),
                        aliases
                    )
                );
            }
        }
    }
}
