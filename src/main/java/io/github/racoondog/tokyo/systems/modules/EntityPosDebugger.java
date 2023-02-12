package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Environment(EnvType.CLIENT)
public class EntityPosDebugger extends Module {
    public static final EntityPosDebugger INSTANCE = new EntityPosDebugger();

    private EntityPosDebugger() {
        super(Tokyo.CATEGORY, "entity-position-debugger", "");
    }

    private final Map<UUID, Integer> playerStates = new HashMap<>();
    private final Map<UUID, List<Vec3d>> playerRenderPosMap = new HashMap<>();

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        for (var player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            Vec3d currentPos = player.getPos();
            Vec3d trackedPos = player.getTrackedPosition().withDelta(0L, 0L, 0L);
            Vec3d pseudoTrackedPos = repack(currentPos);
            //int state = playerStates.getOrDefault(player.getUuid(), -1);

            if (!trackedPos.equals(pseudoTrackedPos)) ChatUtils.info("", "%s weird track?? %s", player.getEntityName(), vecToString(trackedPos.subtract(pseudoTrackedPos)));

            /*
            if (!currentPos.equals(trackedPos)) ChatUtils.info("%s, %s tracked diff is %s.", state, player.getEntityName(), vecToString(currentPos.subtract(trackedPos)));
            List<Vec3d> renderPosList = playerRenderPosMap.get(player.getUuid());
            if (renderPosList != null) {
                for (var it = renderPosList.iterator(); it.hasNext();) {
                    Vec3d renderPos = it.next();
                    if (!currentPos.equals(renderPos)) ChatUtils.info("%s, %s render diff is %s.", state, player.getEntityName(), vecToString(currentPos.subtract(renderPos)));
                    it.remove();
                }
            }
             */
        }
    }

    private static Vec3d repack(Vec3d old) {
        return new Vec3d(
            unpack(pack(old.x)),
            unpack(pack(old.y)),
            unpack(pack(old.z))
        );
    }

    private static long pack(double value) {
        return Math.round(value * 4096.0);
    }

    private static double unpack(long value) {
        return (double)value / 4096.0;
    }

    private static String vecToString(Vec3d vec) {
        return "%s, %s, %s".formatted(normalize(vec.x), normalize(vec.y), normalize(vec.z));
    }

    private static String normalize(double pre) {
        DecimalFormat df = new DecimalFormat("0.00000");
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(Math.abs(pre));
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!Utils.canUpdate()) return;

        for (var player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            playerRenderPosMap.computeIfAbsent(player.getUuid(), o -> new ArrayList<>()).add(player.getPos());
        }
    }

    @EventHandler
    private void onPacket(PacketEvent.Receive event) {
        Packet<?> packet = event.packet;
        if (packet instanceof EntityS2CPacket entityPacket) {
            Entity entity = entityPacket.getEntity(mc.world);
            if (entity instanceof PlayerEntity playerEntity) {
                playerStates.put(playerEntity.getUuid(), 0);
            }
        } else if (packet instanceof EntityPositionS2CPacket entityPositionPacket) {
            Entity entity = mc.world.getEntityById(entityPositionPacket.getId());
            if (entity instanceof PlayerEntity playerEntity) {
                playerStates.put(playerEntity.getUuid(), 1);
            }
        }
    }
}
