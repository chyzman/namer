package com.chyzman.namer.pond;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerManagerDuck {

    @Nullable ServerPlayerEntity namer$getPlayerByNick(String nick);
}
