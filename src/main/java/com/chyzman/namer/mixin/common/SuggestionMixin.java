package com.chyzman.namer.mixin.common;


import com.chyzman.namer.impl.AdvancedSuggestion;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.suggestion.Suggestion;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Suggestion.class)
public abstract class SuggestionMixin {

    @WrapOperation(method = "apply", at = @At(value = "FIELD", target = "Lcom/mojang/brigadier/suggestion/Suggestion;text:Ljava/lang/String;", opcode = Opcodes.GETFIELD), remap = false)
    public String applyNickSuggestion(Suggestion instance, Operation<String> original) {
        if (instance instanceof AdvancedSuggestion advancedSuggestion) return advancedSuggestion.getCompletion();
        return original.call(instance);
    }

    @WrapOperation(method = "expand", at = @At(value = "FIELD", target = "Lcom/mojang/brigadier/suggestion/Suggestion;text:Ljava/lang/String;", opcode = Opcodes.GETFIELD), remap = false)
    public String expandNickSuggestion(Suggestion instance, Operation<String> original) {
        if (instance instanceof AdvancedSuggestion advancedSuggestion) return advancedSuggestion.getCompletion();
        return original.call(instance);
    }
}
