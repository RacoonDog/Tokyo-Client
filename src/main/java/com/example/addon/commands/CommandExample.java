package com.example.addon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@Environment(EnvType.CLIENT)
public class CommandExample extends Command {
    public CommandExample() {
        super("example", "Sends a message.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("hi");
            return SINGLE_SUCCESS;
        });
    }
}
