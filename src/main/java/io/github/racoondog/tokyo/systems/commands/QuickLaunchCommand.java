package io.github.racoondog.tokyo.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.racoondog.meteorsharedaddonutils.features.arguments.AccountArgumentType;
import io.github.racoondog.meteorsharedaddonutils.mixin.mixin.ISwarm;
import io.github.racoondog.tokyo.mixininterface.IClientCommandSource;
import io.github.racoondog.tokyo.gui.MultiInstanceScreen;
import io.github.racoondog.tokyo.utils.AccountUtil;
import io.github.racoondog.tokyo.utils.InstanceBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.Swarm;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class QuickLaunchCommand extends Command {
    public static final QuickLaunchCommand INSTANCE = new QuickLaunchCommand();

    private QuickLaunchCommand() {
        super("quick-launch", "Quickly launch another instance of Minecraft with the specified configurations.", "ql", "launch");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralCommandNode<CommandSource> root = Commands.DISPATCHER.register(builder);

        builder.then(literal("run").executes(this::execute));

        builder.then(literal("-join")
            .redirect(root, ctx -> modifySource(ctx, "join", true)));

        builder.then(literal("-account")
            .then(argument("account", AccountArgumentType.create())
                .redirect(root, ctx -> modifySource(ctx, "account", AccountArgumentType.get(ctx)))));

        builder.then(literal("-swarm")
            .redirect(root, ctx -> modifySource(ctx, "swarm", true)));
    }

    private CommandSource modifySource(CommandContext<CommandSource> ctx, String id, Object value) {
        ClientCommandSource source = (ClientCommandSource) ctx.getSource();
        IClientCommandSource.setMeta(source, id, value);
        return source;
    }

    @Nullable
    private Object meta(CommandContext<CommandSource> ctx, String id) {
        return IClientCommandSource.getMeta((ClientCommandSource) ctx.getSource(), id);
    }

    private Object metaOrDefault(CommandContext<CommandSource> ctx, String id, Object defaultValue) {
        Object returnValue = meta(ctx, id);
        return returnValue == null ? defaultValue : returnValue;
    }

    private int execute(CommandContext<CommandSource> ctx) {
        boolean join = (boolean) metaOrDefault(ctx, "join", false);
        Account<?> account = (Account<?>) metaOrDefault(ctx, "account", AccountUtil.getSelectedAccount());
        boolean swarm = (boolean) metaOrDefault(ctx, "swarm", false);

        MeteorExecutor.execute(() -> {
            InstanceBuilder builder = new InstanceBuilder(account);
            builder.addArg("tokyo?freezeSettings");

            if (join) configureJoin(builder);
            if (swarm) {
                builder.modifyArg("--tokyo?swarmMode", MultiInstanceScreen.SwarmMode.Off.name().toLowerCase(Locale.ROOT));
                builder.modifyArg("--tokyo?swarmIp", ((ISwarm) Modules.get().get(Swarm.class)).getIpAddress().get());
                builder.modifyArg("--tokyo?swarmPort", ((ISwarm) Modules.get().get(Swarm.class)).getServerPort().toString());
            }

            builder.start();
        });

        info("Starting instance...");

        return SINGLE_SUCCESS;
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

        builder.modifyArg("--quickPlayMultiplayer", serverInfo.address);
    }
}

