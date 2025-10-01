package com.chyzman.namer.cca;

import com.chyzman.namer.util.NickFormatter;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;

public class NickStorage implements Component, AutoSyncedComponent {
    private final Scoreboard holder;
    @Nullable
    public final MinecraftServer server;
    private HashMap<UUID, String> nicks = new HashMap<>();

    //region ENDEC STUFF

    private static final Endec<HashMap<UUID, String>> ENDEC = Endec.map(BuiltInEndecs.UUID, Endec.STRING).xmap(HashMap::new, hashMap -> hashMap);

    @Override
    public void readData(ReadView readView) {
        this.nicks = readView.read("nicks", CodecUtils.toCodec(ENDEC)).orElse(new HashMap<>());
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.put("nicks", CodecUtils.toCodec(ENDEC), this.nicks);
    }

    //endregion

    public NickStorage(Scoreboard holder, @Nullable MinecraftServer server) {
        this.holder = holder;
        this.server = server;
    }

    public void setNick(ServerPlayerEntity player, String nick) {
        this.nicks.put(player.getUuid(), nick);
        syncPlayerNick(player);
    }

    public void clearNick(ServerPlayerEntity player) {
        this.nicks.remove(player.getUuid());
        syncPlayerNick(player);
    }

    public void syncPlayerNick(ServerPlayerEntity player) {
        NICK_STORAGE.sync(holder);
        if (server == null) return;
        Objects.requireNonNull(server).getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
    }

    @Nullable
    public Text getNick(UUID uuid) {
        var nick = this.nicks.get(uuid);
        return nick == null ? null : NickFormatter.parseNick(nick);
    }
}
