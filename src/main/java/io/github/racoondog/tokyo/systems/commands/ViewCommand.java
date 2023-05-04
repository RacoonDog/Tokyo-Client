package io.github.racoondog.tokyo.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.racoondog.tokyo.utils.commands.EntityUuidArgumentType;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.*;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class ViewCommand extends Command {
    public static final ViewCommand INSTANCE = new ViewCommand();

    private ViewCommand() {
        super("view", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            ChatUtils.info("Current camera entity: (highlight)%s(default).".formatted(mc.cameraEntity.getEntityName()));
            return SINGLE_SUCCESS;
        });

        builder.then(literal("reset").executes(ctx -> {
            if (mc.cameraEntity == mc.player) ChatUtils.info("Camera was already reset.");
            else {
                MeteorClient.EVENT_BUS.unsubscribe(this);
                mc.setCameraEntity(mc.player);
                ChatUtils.info("Reset camera.");
            }
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("as").then(argument("entity", EntityUuidArgumentType.create()).executes(ctx -> {
            Entity target = EntityUuidArgumentType.get(ctx);
            if (target == mc.cameraEntity) ChatUtils.info("Did not change camera entity.");
            else {
                MeteorClient.EVENT_BUS.subscribe(this);
                mc.setCameraEntity(target);
                ChatUtils.info("Set camera entity to (highlight)%s(default).".formatted(target.getEntityName()));
            }

            return SINGLE_SUCCESS;
        })));
    }

    @EventHandler
    private void onGameLeave(GameLeftEvent event) {
        MeteorClient.EVENT_BUS.unsubscribe(this);
    }

    /**
     * {@link PlayerInteractEntityC2SPacket} are cancelled due to interacted where the spectated entity was looking at. Kicked when interacting with {@code mc.player}.
     * {@link PlayerInteractBlockC2SPacket} are cancelled due to interacted where the spectated entity was looking at.
     * {@link PlayerInteractItemC2SPacket} are cancelled due to interacted where the spectated entity was looking at.
     * Other packets are cancelled out of caution.
     * {@link PlayerMoveC2SPacket} are not cancelled as they are sent each second even when stationary, and you cannot move when spectating.
     */
    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof PlayerActionC2SPacket ||
            event.packet instanceof PlayerInputC2SPacket ||
            event.packet instanceof PlayerInteractEntityC2SPacket ||
            event.packet instanceof PlayerInteractBlockC2SPacket ||
            event.packet instanceof PlayerInteractItemC2SPacket)
            event.cancel();
    }
}
