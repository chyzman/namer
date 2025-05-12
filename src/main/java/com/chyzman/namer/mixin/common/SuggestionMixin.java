package com.chyzman.namer.mixin.common;


import com.chyzman.namer.impl.NickSuggestion;
import com.chyzman.namer.pond.CommandSourceDuck;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Collection;
import java.util.List;

@Mixin(Suggestion.class)
public abstract class SuggestionMixin {

    @WrapOperation(method = "apply", at = @At(value = "FIELD", target = "Lcom/mojang/brigadier/suggestion/Suggestion;text:Ljava/lang/String;", opcode = Opcodes.GETFIELD), remap = false)
    public String applyNickSuggestion(Suggestion instance, Operation<String> original) {
        if (instance instanceof NickSuggestion nickSuggestion) return nickSuggestion.getCompletion();
        return original.call(instance);
    }

    @WrapOperation(method = "expand", at = @At(value = "FIELD", target = "Lcom/mojang/brigadier/suggestion/Suggestion;text:Ljava/lang/String;", opcode = Opcodes.GETFIELD), remap = false)
    public String expandNickSuggestion(Suggestion instance, Operation<String> original) {
        if (instance instanceof NickSuggestion nickSuggestion) return nickSuggestion.getCompletion();
        return original.call(instance);
    }
}
