package io.github.racoondog.tokyo.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class UuidUtils {
    private static final Map<UUID, String> hashCache = new HashMap<>();

    public static UUID getCurrentUuid() {
        return MinecraftClient.getInstance().getNetworkHandler().getProfile().getId();
    }

    public static String hashUuid(UUID uuid) {
        return hashCache.computeIfAbsent(uuid, obj -> {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
            buffer.putLong(obj.getLeastSignificantBits());
            buffer.putLong(obj.getMostSignificantBits());
            return DigestUtils.sha1Hex(buffer.array());
        });
    }

    public static String hashCurrentUuid() {
        return hashUuid(getCurrentUuid());
    }
}
