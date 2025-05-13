package com.chyzman.namer;

import com.chyzman.namer.command.NickCommand;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Namer implements ModInitializer {
    public static final String MODID = "namer";

    @Override
    public void onInitialize() {
        NickCommand.register();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
