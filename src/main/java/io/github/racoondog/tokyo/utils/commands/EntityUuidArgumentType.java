package io.github.racoondog.tokyo.utils.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class EntityUuidArgumentType implements ArgumentType<Entity> {
    private static final DynamicCommandExceptionType NO_SUCH_ENTITY = new DynamicCommandExceptionType((id) -> {
        return Text.literal("Entity with identifier " + id + " doesn't exist.");
    });

    public static EntityUuidArgumentType create() {
        return new EntityUuidArgumentType();
    }

    public static Entity get(CommandContext<?> context) {
        return context.getArgument("entity", Entity.class);
    }

    public static Entity get(String name, CommandContext<?> context) {
        return context.getArgument(name, Entity.class);
    }

    @Nullable
    @Override
    public Entity parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readUnquotedString();

        for (var entity : mc.world.getEntities()) {
            if (argument.equals(entity.getEntityName())) return entity;
        }

        throw NO_SUCH_ENTITY.create(argument);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> possibleSuggestions = new ArrayList<>();

        for (var entity : mc.world.getEntities()) {
            possibleSuggestions.add(entity.getEntityName());
        }

        return CommandSource.suggestMatching(possibleSuggestions, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of(mc.player.getEntityName(), mc.player.getUuidAsString());
    }
}
