package com.chyzman.namer.mixin.common;

import com.chyzman.namer.pond.EntitySelectorDuck;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.FabricEntitySelectorReader;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.chyzman.namer.Namer.SELECTING_BY_NICK_FLAG;

@Mixin(EntitySelectorReader.class)
public abstract class EntitySelectorReaderMixin {

    @Shadow @Final private StringReader reader;

    @Shadow private boolean includesNonPlayers;

    @Shadow private @Nullable String playerName;

    @Shadow private int limit;

    @Inject(method = "readRegular", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;getCursor()I", ordinal = 0), cancellable = true)
    public void readNicks(CallbackInfo ci) throws CommandSyntaxException {
        var cursor = reader.getCursor();
        var nick = reader.readString();
        if (!reader.canRead() || reader.read() != '*') {
            reader.setCursor(cursor);
            return;
        }

        includesNonPlayers = false;
        playerName = nick;
        ((FabricEntitySelectorReader) this).setCustomFlag(SELECTING_BY_NICK_FLAG, true);
        limit = 1;
        ci.cancel();
    }

    @Inject(method = "build", at = @At("RETURN"), cancellable = true)
    public void applyNickFlag(CallbackInfoReturnable<EntitySelector> cir) {
        if (((FabricEntitySelectorReader) this).getCustomFlag(SELECTING_BY_NICK_FLAG)) {
            cir.setReturnValue(((EntitySelectorDuck)cir.getReturnValue()).namer$playerNameIsNick(true));
        }
    }
}
