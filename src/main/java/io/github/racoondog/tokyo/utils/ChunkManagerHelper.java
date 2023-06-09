package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.mixin.IClientChunkManager;
import io.github.racoondog.tokyo.mixin.IClientChunkMap;
import io.github.racoondog.tokyo.mixin.IClientPlayNetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class ChunkManagerHelper {
    public static void readLightData(int chunkX, int chunkZ, LightData lightData) {
        ((IClientPlayNetworkHandler) MinecraftClient.getInstance().getNetworkHandler()).tokyo$invokeReadLightData(chunkX, chunkZ, lightData);
    }

    public static void scheduleRenderChunk(WorldChunk chunk, int chunkX, int chunkZ) {
        ((IClientPlayNetworkHandler) MinecraftClient.getInstance().getNetworkHandler()).tokyo$invokeScheduleRenderChunk(chunk, chunkX, chunkZ);
    }

    private static ClientChunkManager.ClientChunkMap getChunkMap() {
        return ((IClientChunkManager) MinecraftClient.getInstance().world.getChunkManager()).tokyo$getChunks();
    }

    private static int getChunkIndex(int chunkX, int chunkZ) {
        return ((IClientChunkMap) (Object) getChunkMap()).tokyo$invokeGetIndex(chunkX, chunkZ);
    }

    private static WorldChunk getChunk(int chunkIndex) {
        return ((IClientChunkMap) (Object) getChunkMap()).tokyo$getChunks().get(chunkIndex);
    }

    private static void setChunk(int chunkIndex, WorldChunk chunk) {
        ((IClientChunkMap) (Object) getChunkMap()).tokyo$invokeSet(chunkIndex, chunk);
    }

    /**
     * Atomics...
     * - Crosby
     */
    private static boolean positionEquals(WorldChunk chunk, int chunkX, int chunkZ) {
        return IClientChunkManager.tokyo$invokePositionEquals(chunk, chunkX, chunkZ);
    }

    public static void loadChunk(int chunkX, int chunkZ, PacketByteBuf rawSectionData, NbtCompound heightmapData, Consumer<ChunkData.BlockEntityVisitor> blockEntityConsumer) {
        //todo range check
        int index = getChunkIndex(chunkX, chunkZ);
        WorldChunk worldChunk = getChunk(index);
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        if (!positionEquals(worldChunk, chunkX, chunkZ)) {
            worldChunk = new WorldChunk(MinecraftClient.getInstance().world, chunkPos);
            worldChunk.loadFromPacket(rawSectionData, heightmapData, blockEntityConsumer);
            setChunk(index, worldChunk);
        } else worldChunk.loadFromPacket(rawSectionData, heightmapData, blockEntityConsumer);
        MinecraftClient.getInstance().world.resetChunkColor(chunkPos);
    }
}
