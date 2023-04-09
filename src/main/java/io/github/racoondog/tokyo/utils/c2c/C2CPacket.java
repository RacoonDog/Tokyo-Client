package io.github.racoondog.tokyo.utils.c2c;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public interface C2CPacket {
    void write(PacketByteBuf buf);
    void apply(C2CPacketListener listener);
}
