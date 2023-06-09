package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(ClientChunkManager.class)
public interface IClientChunkManager {
    @Accessor("chunks")
    ClientChunkManager.ClientChunkMap tokyo$getChunks();

    @Invoker("positionEquals")
    static boolean tokyo$invokePositionEquals(WorldChunk chunk, int chunkX, int chunkZ) {
        throw new AssertionError();
    }
}
