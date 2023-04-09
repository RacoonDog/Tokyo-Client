package io.github.racoondog.tokyo.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.racoondog.tokyo.utils.c2c.C2CNetworkHandler;
import io.github.racoondog.tokyo.utils.c2c.packets.WaypointC2CPacket;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.systems.commands.arguments.WaypointArgumentType;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ShareCommand extends Command {
    public static final ShareCommand INSTANCE = new ShareCommand();

    private ShareCommand() {
        super("share", "Send information directly to other Tokyo clients.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
            argument("waypoint", WaypointArgumentType.create())
                .executes(ShareCommand::shareWaypointAll)
                .then(argument("player", PlayerListEntryArgumentType.create())
                    .executes(ShareCommand::shareWaypointPlayer)
                )
        );
    }

    private static int shareWaypointAll(CommandContext<CommandSource> ctx) {
        Waypoint waypoint = WaypointArgumentType.get(ctx); //todo this should have a version that takes a param too

        C2CNetworkHandler.INSTANCE.sendPacket(new WaypointC2CPacket(waypoint));

        return SINGLE_SUCCESS;
    }

    private static int shareWaypointPlayer(CommandContext<CommandSource> ctx) {
        Waypoint waypoint = WaypointArgumentType.get(ctx);
        PlayerListEntry target = PlayerListEntryArgumentType.get(ctx);

        C2CNetworkHandler.INSTANCE.sendPacket(new WaypointC2CPacket(waypoint), target);

        return SINGLE_SUCCESS;
    }
}
