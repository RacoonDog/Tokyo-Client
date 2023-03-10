package io.github.racoondog.tokyo.mixin.oldchunks;

import io.github.racoondog.tokyo.mixininterface.IChunkDeltaUpdateS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(ChunkDeltaUpdateS2CPacket.class)
public abstract class ChunkDeltaUpdateS2CPacketMixin implements IChunkDeltaUpdateS2CPacket {
    @Shadow @Final private short[] positions;
    @Shadow @Final private ChunkSectionPos sectionPos;
    @Shadow @Final private BlockState[] blockStates;

    @Override
    public short[] tokyo$getPosition() {
        return this.positions;
    }

    @Override
    public ChunkSectionPos tokyo$getSectionPos() {
        return this.sectionPos;
    }

    @Override
    public BlockState[] tokyo$getBlockStates() {
        return this.blockStates;
    }
}
