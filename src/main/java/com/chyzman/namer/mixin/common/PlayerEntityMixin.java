package com.chyzman.namer.mixin.common;

import com.chyzman.namer.pond.PlayerEntityDuck;
import com.chyzman.namer.util.NickFormatter;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements PlayerEntityDuck {
    @Unique private boolean ignoreNick = false;

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract Scoreboard getScoreboard();

    @Shadow public abstract Text getDisplayName();

    @ModifyExpressionValue(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getName()Lnet/minecraft/text/Text;"))
    private Text makeNicksWork(Text original) {
        if (ignoreNick) return original;
        if (getScoreboard() == null) return original;

        var storage = NICK_STORAGE.getNullable(getScoreboard());
        if (storage == null) return original;

        var nick = storage.getNick(getUuid());
        if (nick == null) return original;

        return nick;
    }

    @Override
    public Text namer$getActualDisplayName() {
        ignoreNick = true;
        var name = getDisplayName();
        ignoreNick = false;
        return name;
    }
}
