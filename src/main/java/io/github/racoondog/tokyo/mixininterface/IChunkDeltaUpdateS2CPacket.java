package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.ChunkSectionPos;

@Environment(EnvType.CLIENT)
public interface IChunkDeltaUpdateS2CPacket {
    short[] _getPosition();
    ChunkSectionPos _getSectionPos();
    BlockState[] _getBlockStates();

    static short[] getPosition(ChunkDeltaUpdateS2CPacket packet) {
        return ((IChunkDeltaUpdateS2CPacket) packet)._getPosition();
    }

    static ChunkSectionPos getSectionPos(ChunkDeltaUpdateS2CPacket packet) {
        return ((IChunkDeltaUpdateS2CPacket) packet)._getSectionPos();
    }

    static BlockState[] getBlockStates(ChunkDeltaUpdateS2CPacket packet) {
        return ((IChunkDeltaUpdateS2CPacket) packet)._getBlockStates();
    }
}
