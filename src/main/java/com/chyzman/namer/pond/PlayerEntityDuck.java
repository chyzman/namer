package com.chyzman.namer.pond;

import net.minecraft.text.Text;

public interface PlayerEntityDuck {
    default Text namer$getActualDisplayName() {
        throw new UnsupportedOperationException("Implemented by Mixin");
    }
}
