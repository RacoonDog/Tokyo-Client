package io.github.racoondog.tokyo.utils.c2c.packets;

import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class InputSyncMouseMoveC2CPacket implements C2CPacket {
    public final double x;
    public final double y;

    public InputSyncMouseMoveC2CPacket(PacketByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
    }

    public InputSyncMouseMoveC2CPacket(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onInputSyncMouseMoveC2CPacket(this);
    }
}
