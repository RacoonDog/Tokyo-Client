package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Environment(EnvType.CLIENT)
@Mixin(ClientChunkManager.ClientChunkMap.class)
public interface IClientChunkMap {
    @Invoker("getIndex")
    int tokyo$invokeGetIndex(int chunkX, int chunkZ);

    @Invoker("set")
    void tokyo$invokeSet(int chunkIndex, WorldChunk chunk);

    @Accessor("chunks")
    AtomicReferenceArray<WorldChunk> tokyo$getChunks();
}
