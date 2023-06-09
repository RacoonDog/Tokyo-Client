package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public interface IClientPlayNetworkHandler {
    @Invoker("readLightData")
    void tokyo$invokeReadLightData(int x, int z, LightData lightData);

    @Invoker("scheduleRenderChunk")
    void tokyo$invokeScheduleRenderChunk(WorldChunk chunk, int x, int z);
}
