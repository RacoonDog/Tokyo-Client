package io.github.racoondog.tokyo.commands;

import meteordevelopment.meteorclient.commands.Commands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class TokyoCommands {
    public static void init() {
        Commands.add(new GetTokenCommand());
        Commands.add(new ItemCommand());
        Commands.add(new QuickLaunchCommand());
        Commands.add(new LookAtCommand());
    }
}
