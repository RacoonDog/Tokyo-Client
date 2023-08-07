package io.github.racoondog.tokyo.utils.c2c.packets;

import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class InputSyncMouseScrollC2CPacket implements C2CPacket {
    public final double horizontalScroll;
    public final double verticalScroll;

    public InputSyncMouseScrollC2CPacket(PacketByteBuf buf) {
        horizontalScroll = buf.readDouble();
        verticalScroll = buf.readDouble();
    }

    public InputSyncMouseScrollC2CPacket(double horizontalScroll, double verticalScroll) {
        this.horizontalScroll = horizontalScroll;
        this.verticalScroll = verticalScroll;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeDouble(horizontalScroll);
        buf.writeDouble(verticalScroll);
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onInputSyncMouseScrollC2CPacket(this);
    }
}
