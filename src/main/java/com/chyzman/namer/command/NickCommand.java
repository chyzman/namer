package com.chyzman.namer.command;

import com.chyzman.namer.cca.component.NickStorage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NickCommand {
    private static final DynamicCommandExceptionType SET_FAIL = new DynamicCommandExceptionType(a -> Text.translatable("command.namer.nick.set.fail", a));
    private static final Dynamic2CommandExceptionType SET_FAIL_OTHER = new Dynamic2CommandExceptionType((a, b) -> Text.translatable("command.namer.nick.set.fail.other", a, b));

    private static final SimpleCommandExceptionType SET_FAIL_BROKEN = new SimpleCommandExceptionType(SET_FAIL.create(Text.translatable("command.namer.nick.set.fail.broken")).getRawMessage());
    private static final SimpleCommandExceptionType SET_FAIL_UNIQUE = new SimpleCommandExceptionType(SET_FAIL.create(Text.translatable("command.namer.nick.set.fail.unique")).getRawMessage());

    private static final DynamicCommandExceptionType SET_FAIL_BROKEN_OTHER = new DynamicCommandExceptionType((a) -> SET_FAIL_OTHER.create(a, Text.translatable("command.namer.nick.set.fail.broken")).getRawMessage());
    private static final DynamicCommandExceptionType SET_FAIL_UNIQUE_OTHER = new DynamicCommandExceptionType((a) -> SET_FAIL_OTHER.create(a, Text.translatable("command.namer.nick.set.fail.unique")).getRawMessage());


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                literal("nick")
                    .executes(context -> setNick(
                        context.getSource(),
                        context.getSource().getPlayerOrThrow(),
                        null
                    ))
                    .then(argument("nickname", StringArgumentType.greedyString())
                              .executes(context -> setNick(
                                            context.getSource(),
                                            context.getSource().getPlayerOrThrow(),
                                            Text.literal(StringArgumentType.getString(context, "nickname"))
                                        )
                              )
                    )
            );
        });
    }

    public static int setNick(ServerCommandSource source, ServerPlayerEntity target, @Nullable Text nick) throws CommandSyntaxException {
        try {
            var storage = NICK_STORAGE.getNullable(source.getServer().getScoreboard());
            var self = Objects.equals(source.getEntity(), target);
            if (storage == null) throw self ? SET_FAIL_BROKEN.create() : SET_FAIL_BROKEN_OTHER.create(target.getName());
            if (nick == null || NickStorage.raw(nick).isBlank()) {
                storage.clearNick(target);
                source.sendFeedback(() -> self ? Text.translatable("command.namer.nick.clear.success") : Text.translatable("command.namer.nick.clear.success.other", target.getName()), true);
                return 1;
            }
            if (!storage.isUnique(nick)) throw self ? SET_FAIL_UNIQUE.create() : SET_FAIL_UNIQUE_OTHER.create(target.getName());
            storage.setNick(target, nick);
            source.sendFeedback(() -> self ? Text.translatable("command.namer.nick.set.success", nick) : Text.translatable("command.namer.nick.set.success.other", target.getName(), nick), true);
        } catch (Exception e) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal(e.getLocalizedMessage()));
            MinecraftClient.getInstance().player.sendMessage(Text.literal(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList().toString()));
        }
        return 1;
    }
}
