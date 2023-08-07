package io.github.racoondog.tokyo.utils.c2c.packets;

import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class InputSyncKeyPressC2CPacket implements C2CPacket {
    public final int key;
    public final int scancode;
    public final int action;
    public final int modifiers;

    public InputSyncKeyPressC2CPacket(PacketByteBuf buf) {
        key = buf.readInt();
        scancode = buf.readInt();
        action = buf.readInt();
        modifiers = buf.readInt();
    }

    public InputSyncKeyPressC2CPacket(int key, int scancode, int action, int modifiers) {
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.modifiers = modifiers;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(key);
        buf.writeInt(scancode);
        buf.writeInt(action);
        buf.writeInt(modifiers);
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onInputSyncKeyPressC2CPacket(this);
    }
}
