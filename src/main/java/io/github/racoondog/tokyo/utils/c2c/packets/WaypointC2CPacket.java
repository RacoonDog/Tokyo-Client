package io.github.racoondog.tokyo.utils.c2c.packets;

import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketListener;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class WaypointC2CPacket implements C2CPacket {
    public final Waypoint waypoint;

    public WaypointC2CPacket(PacketByteBuf buf) {
        this.waypoint = new Waypoint.Builder()
            .pos(buf.readBlockPos())
            .name(buf.readString())
            .dimension(buf.readEnumConstant(Dimension.class))
            .build();
    }

    public WaypointC2CPacket(Waypoint waypoint) {
        this.waypoint = waypoint;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(waypoint.pos.get());
        buf.writeString(waypoint.name.get());
        buf.writeEnumConstant(waypoint.dimension.get());
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onWaypointC2CPacket(this);
    }
}
