package io.github.racoondog.tokyo.mixin.oldchunks;

import io.github.racoondog.tokyo.systems.modules.OldChunks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Redirect(method = "loadChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientChunkManager;loadChunkFromPacket(IILnet/minecraft/network/PacketByteBuf;Lnet/minecraft/nbt/NbtCompound;Ljava/util/function/Consumer;)Lnet/minecraft/world/chunk/WorldChunk;"))
    private WorldChunk newChunks$computeChunk(ClientChunkManager instance, int x, int z, PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer) {
        WorldChunk chunk = instance.loadChunkFromPacket(x, z, buf, nbt, consumer);
        if (OldChunks.INSTANCE.isActive() && OldChunks.INSTANCE.blockCheck.get()) OldChunks.onChunkDataPacketReceive(chunk);
        return chunk;
    }
}
