package com.chyzman.namer.command;

import com.chyzman.namer.Namer;
import com.chyzman.namer.util.NickFormatter;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.chyzman.namer.registry.CardinalComponentsRegistry.NICK_STORAGE;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NickCommand {
    private static final DynamicCommandExceptionType SET_FAIL = new DynamicCommandExceptionType(o -> Text.translatable("command.namer.nick.set.fail", o));
    private static final Dynamic2CommandExceptionType SET_FAIL_OTHER = new Dynamic2CommandExceptionType((a, b) -> Text.translatable("command.namer.nick.set.fail.other", a, b));

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
                                            StringArgumentType.getString(context, "nickname")
                                        )
                              )
                    )
            );
        });
    }

    public static int setNick(ServerCommandSource source, ServerPlayerEntity target, @Nullable String nick) throws CommandSyntaxException {
        var server = source.getServer();
        var storage = NICK_STORAGE.getNullable(server.getScoreboard());
        var self = Objects.equals(source.getEntity(), target);
        if (storage == null) {
            var message = Text.translatable("command.namer.nick.set.fail.unknown");
            throw self ? SET_FAIL.create(message) : SET_FAIL_OTHER.create(target.getName(), message);
        }
        var parsedNick = NickFormatter.parseNick(nick);
        var nickString = parsedNick.getString();
        if (
            nick == null ||
            nickString.isBlank() ||
            parsedNick.equals(target.getName())
        ) {
            storage.clearNick(target);
            source.sendFeedback(() -> self ? Text.translatable("command.namer.nick.clear.success") : Text.translatable("command.namer.nick.clear.success.other", target.getName()), true);
            return 1;
        }
        var maxLength = Namer.CONFIG.maxNickLength();
        if (maxLength > 0 && nickString.length() > maxLength) {
            var message = Text.translatable("command.namer.nick.set.fail.length",parsedNick, maxLength);
            throw self ? SET_FAIL.create(message) : SET_FAIL_OTHER.create(target.getName(), message);
        }
        storage.setNick(target, nick);
        source.sendFeedback(() -> self ? Text.translatable("command.namer.nick.set.success", parsedNick) : Text.translatable("command.namer.nick.set.success.other", target.getName(), parsedNick), true);
        return 1;
    }
}
