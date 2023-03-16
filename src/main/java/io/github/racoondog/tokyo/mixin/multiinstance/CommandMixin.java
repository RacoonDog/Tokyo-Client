package io.github.racoondog.tokyo.mixin.multiinstance;

import com.mojang.brigadier.CommandDispatcher;
import io.github.racoondog.tokyo.mixininterface.ICommand;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Command.class)
public abstract class CommandMixin implements ICommand {
    @Unique private CommandDispatcher<CommandSource> dispatcher;

    @Inject(method = "register", at = @At("HEAD"))
    private void hijackDispatcher(CommandDispatcher<CommandSource> dispatcher, String name, CallbackInfo ci) {
        this.dispatcher = dispatcher;
    }

    @Override
    public CommandDispatcher<CommandSource> tokyo$getDispatcher() {
        return this.dispatcher;
    }
}
