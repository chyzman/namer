package com.chyzman.namer;

import com.chyzman.namer.NamerConfig;
import com.chyzman.namer.cca.NickStorage;
import com.chyzman.namer.command.NickCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

public class Namer implements ModInitializer {
    public static final String MODID = "namer";

    public static final NamerConfig CONFIG = NamerConfig.createAndLoad();

    @Override
    public void onInitialize() {
        NickCommand.register();


        var allowNickFormatting = CONFIG.optionForKey(CONFIG.keys.allowNickFormatting);
        if (allowNickFormatting != null) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> allowNickFormatting.observe(o -> {
                var storage = NICK_STORAGE.getNullable(server.getScoreboard());
                if (storage == null) return;
                for (var player : server.getPlayerManager().getPlayerList()) storage.syncPlayerNick(player);
            }));
        }
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
