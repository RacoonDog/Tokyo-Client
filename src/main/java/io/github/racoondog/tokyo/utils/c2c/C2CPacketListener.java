package io.github.racoondog.tokyo.utils.c2c;

import io.github.racoondog.tokyo.utils.c2c.packets.ChunkC2CPacket;
import io.github.racoondog.tokyo.utils.c2c.packets.WaypointC2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface C2CPacketListener {
    void onWaypointC2CPacket(WaypointC2CPacket packet);
    void onChunkC2CPacket(ChunkC2CPacket packet);
}
