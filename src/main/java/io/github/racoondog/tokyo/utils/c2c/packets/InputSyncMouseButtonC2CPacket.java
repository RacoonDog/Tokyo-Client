package io.github.racoondog.tokyo.utils.c2c.packets;

import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class InputSyncMouseButtonC2CPacket implements C2CPacket {
    public final int button;
    public final int action;
    public final int modifiers;

    public InputSyncMouseButtonC2CPacket(PacketByteBuf buf) {
        this.button = buf.readInt();
        this.action = buf.readInt();
        this.modifiers = buf.readInt();
    }

    public InputSyncMouseButtonC2CPacket(int button, int action, int modifiers) {
        this.button = button;
        this.action = action;
        this.modifiers = modifiers;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(button);
        buf.writeInt(action);
        buf.writeInt(modifiers);
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onInputSyncMouseButtonC2CPacket(this);
    }
}
