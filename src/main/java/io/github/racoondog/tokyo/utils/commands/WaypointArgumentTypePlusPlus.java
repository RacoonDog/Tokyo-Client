package io.github.racoondog.tokyo.utils.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WaypointArgumentTypePlusPlus implements ArgumentType<String> {
    private static final DynamicCommandExceptionType NO_SUCH_WAYPOINT = new DynamicCommandExceptionType(name -> Text.literal("Waypoint with name '" + name + "' doesn't exist."));

    private final boolean greedy;

    private WaypointArgumentTypePlusPlus(boolean greedy) {
        this.greedy = greedy;
    }

    public static WaypointArgumentTypePlusPlus word() {
        return new WaypointArgumentTypePlusPlus(false);
    }

    public static WaypointArgumentTypePlusPlus greedy() {
        return new WaypointArgumentTypePlusPlus(true);
    }

    public static Waypoint get(CommandContext<?> context) {
        return Waypoints.get().get(context.getArgument("waypoint", String.class));
    }

    public static Waypoint get(CommandContext<?> context, String name) {
        return Waypoints.get().get(context.getArgument(name, String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String argument;
        if (greedy) {
            argument = reader.getRemaining();
            reader.setCursor(reader.getTotalLength());
        } else argument = reader.readString();

        if (Waypoints.get().get(argument) == null) throw NO_SUCH_WAYPOINT.create(argument);
        return argument;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(getExamples(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        List<String> names = new ArrayList<>();
        for (Waypoint waypoint : Waypoints.get()) names.add(waypoint.name.get());
        return names;
    }
}
