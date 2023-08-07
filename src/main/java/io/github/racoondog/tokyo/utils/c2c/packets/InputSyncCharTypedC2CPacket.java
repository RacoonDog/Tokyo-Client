package io.github.racoondog.tokyo.utils.c2c.packets;

import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class InputSyncCharTypedC2CPacket implements C2CPacket {
    public final int codePoint;
    public final int modifiers;

    public InputSyncCharTypedC2CPacket(PacketByteBuf buf) {
        codePoint = buf.readInt();
        modifiers = buf.readInt();
    }

    public InputSyncCharTypedC2CPacket(int codePoint, int modifiers) {
        this.codePoint = codePoint;
        this.modifiers = modifiers;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(codePoint);
        buf.writeInt(modifiers);
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onInputSyncCharTypedC2CPacket(this);
    }
}
