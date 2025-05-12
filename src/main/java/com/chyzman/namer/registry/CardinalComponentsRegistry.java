package com.chyzman.namer.registry;

import com.chyzman.namer.cca.NickStorage;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;

import static com.chyzman.namer.Namer.id;

public class CardinalComponentsRegistry implements ScoreboardComponentInitializer {

    public static final ComponentKey<NickStorage> NICK_STORAGE = ComponentRegistry.getOrCreate(id("storage"), NickStorage.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(NICK_STORAGE, NickStorage::new);
    }
}
