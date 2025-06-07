package com.chyzman.namer;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = Namer.MODID)
@Config(name = Namer.MODID, wrapperName = "NamerConfig")
public class NamerConfigModel {

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 0, max = 256)
    public int maxNickLength = 0;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean allowNickFormatting = true;
}
