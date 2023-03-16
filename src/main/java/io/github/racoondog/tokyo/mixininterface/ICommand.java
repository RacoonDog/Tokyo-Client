package io.github.racoondog.tokyo.mixininterface;

import com.mojang.brigadier.CommandDispatcher;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;

@Environment(EnvType.CLIENT)
public interface ICommand {
    CommandDispatcher<CommandSource> tokyo$getDispatcher();

    static CommandDispatcher<CommandSource> getDispatcher(Command command) {
        return ((ICommand) command).tokyo$getDispatcher();
    }
}
