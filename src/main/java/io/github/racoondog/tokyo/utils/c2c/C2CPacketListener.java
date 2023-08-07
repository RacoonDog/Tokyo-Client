package io.github.racoondog.tokyo.utils.c2c;

import io.github.racoondog.tokyo.utils.c2c.packets.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface C2CPacketListener {
    void onWaypointC2CPacket(WaypointC2CPacket packet);
    void onChunkC2CPacket(ChunkC2CPacket packet);
    void onInputSyncMouseButtonC2CPacket(InputSyncMouseButtonC2CPacket packet);
    void onInputSyncMouseScrollC2CPacket(InputSyncMouseScrollC2CPacket packet);
    void onInputSyncMouseMoveC2CPacket(InputSyncMouseMoveC2CPacket packet);
    void onInputSyncKeyPressC2CPacket(InputSyncKeyPressC2CPacket packet);
    void onInputSyncCharTypedC2CPacket(InputSyncCharTypedC2CPacket packet);
}
