package io.github.racoondog.tokyo.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.commands.arguments.PlayerArgumentType;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@Environment(EnvType.CLIENT)
public class ViewCommand extends Command {
    public static final ViewCommand INSTANCE = new ViewCommand();

    private ViewCommand() {
        super("view", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ViewCommand::getView);
        builder.then(literal("reset").executes(ViewCommand::resetView));
        builder.then(literal("as").then(argument("player", PlayerArgumentType.create()).executes(ViewCommand::setView)));
    }

    private static int getView(CommandContext<CommandSource> ctx) {
        String cameraEntity = mc.cameraEntity instanceof PlayerEntity playerEntity ? playerEntity.getEntityName() : mc.cameraEntity.getClass().getSimpleName();
        ChatUtils.info("Current camera entity: " + cameraEntity);
        return SINGLE_SUCCESS;
    }

    private static int resetView(CommandContext<CommandSource> ctx) {
        mc.setCameraEntity(mc.player);
        ChatUtils.info("Reset camera.");
        return SINGLE_SUCCESS;
    }

    private static int setView(CommandContext<CommandSource> ctx) {
        PlayerEntity target = PlayerArgumentType.get(ctx);
        mc.setCameraEntity(target);
        ChatUtils.info("Set camera entity to %s.".formatted(target.getEntityName()));
        return SINGLE_SUCCESS;
    }
}
