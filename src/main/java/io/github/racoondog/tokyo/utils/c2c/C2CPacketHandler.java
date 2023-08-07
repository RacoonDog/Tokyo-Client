package io.github.racoondog.tokyo.utils.c2c;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.utils.c2c.packets.*;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.meteorclient.utils.PreInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class C2CPacketHandler {
    private static final Object2IntMap<Class<? extends C2CPacket>> packetIds = Util.make(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));
    private static final List<Function<PacketByteBuf, ? extends C2CPacket>> packetFactories = new ArrayList<>();

    @PreInit
    public static void preInit() {
        C2CPacketHandler.register(WaypointC2CPacket.class, p -> new WaypointC2CPacket((PacketByteBuf) p));
        C2CPacketHandler.register(InputSyncMouseButtonC2CPacket.class, p -> new InputSyncMouseButtonC2CPacket((PacketByteBuf) p));
        C2CPacketHandler.register(InputSyncMouseMoveC2CPacket.class, p -> new InputSyncMouseMoveC2CPacket((PacketByteBuf) p));
        C2CPacketHandler.register(InputSyncMouseScrollC2CPacket.class, p -> new InputSyncMouseScrollC2CPacket((PacketByteBuf) p));
        C2CPacketHandler.register(InputSyncKeyPressC2CPacket.class, p -> new InputSyncKeyPressC2CPacket((PacketByteBuf) p));
        C2CPacketHandler.register(InputSyncCharTypedC2CPacket.class, p -> new InputSyncCharTypedC2CPacket((PacketByteBuf) p));
    }

    public static <P extends C2CPacket> void register(Class<P> packet, Function<PacketByteBuf, P> packetFactory) {
        int id = packetFactories.size();
        int i = packetIds.put(packet, id);
        if (i != -1) {
            Tokyo.LOG.warn("Packet '{}' already registered to ID {}.", packet.getSimpleName(), i);
        } else packetFactories.add(packetFactory);
    }

    /**
     * @return -1 if not found.
     */
    public static <P extends C2CPacket> int getId(Class<P> packet) {
        return packetIds.getInt(packet);
    }

    @Nullable
    public static C2CPacket createPacket(int id, PacketByteBuf buf) {
        Function<PacketByteBuf, ? extends C2CPacket> function = packetFactories.get(id);
        return function == null ? null : function.apply(buf);
    }

    public static PacketByteBuf createBuf() {
        return new PacketByteBuf(Unpooled.buffer(16));
    }

    public static PacketByteBuf writePacket(C2CPacket packet) {
        int id = C2CPacketHandler.getId(packet.getClass());
        if (id == -1) {
            throw new RuntimeException("Unregistered packet '%s'!".formatted(packet.getClass().getSimpleName()));
        }

        PacketByteBuf buf = createBuf();
        buf.writeInt(id);
        packet.write(buf);

        return buf;
    }
}
