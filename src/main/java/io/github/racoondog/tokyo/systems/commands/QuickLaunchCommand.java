package io.github.racoondog.tokyo.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.racoondog.meteorsharedaddonutils.features.arguments.AccountArgumentType;
import io.github.racoondog.tokyo.utils.AccountUtil;
import io.github.racoondog.tokyo.utils.InstanceBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;

@Environment(EnvType.CLIENT)
public class QuickLaunchCommand extends Command {
    public static final QuickLaunchCommand INSTANCE = new QuickLaunchCommand();

    private QuickLaunchCommand() {
        super("quick-launch", "Quickly launch another instance of Minecraft with the specified configurations.", "ql", "launch");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> execute(ctx, false))
            .then(literal("-join").executes(ctx -> execute(ctx, true)));

        builder.then(argument("account", AccountArgumentType.create()).executes(ctx -> executeAccount(ctx, false))
            .then(literal("-join").executes(ctx -> executeAccount(ctx, true))));
    }

    private int execute(CommandContext<CommandSource> ctx, boolean join) {
        MeteorExecutor.execute(() -> {
            InstanceBuilder builder = new InstanceBuilder(AccountUtil.getSelectedAccount());

            if (join) configureJoin(builder);

            builder.start();
        });

        info("Starting instance...");

        return 1;
    }

    private int executeAccount(CommandContext<CommandSource> ctx, boolean join) {
        MeteorExecutor.execute(() -> {
            InstanceBuilder builder = new InstanceBuilder(AccountArgumentType.get(ctx));

            if (join) configureJoin(builder);

            builder.start();
        });

        info("Starting instance...");

        return 1;
    }

    private void configureJoin(InstanceBuilder builder) {
        if (mc.isIntegratedServerRunning()) {
            warning("Cannot join singleplayer world.");
            return;
        }

        ServerInfo serverInfo = mc.getCurrentServerEntry();

        if (serverInfo == null) {
            warning("Could not obtain server information.");
            return;
        }

        String[] address = serverInfo.address.split(":");

        builder.modifyArg("--server", address[0])
            .modifyArg("--port", address.length > 1 ? address[1] : "25565");
    }
}

