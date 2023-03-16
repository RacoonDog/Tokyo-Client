package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientCommandSource;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface IClientCommandSource {
    @Nullable
    Object tokyo$getMeta(String id);
    void tokyo$setMeta(String id, Object value);

    @Nullable
    static Object getMeta(ClientCommandSource source, String id) {
        return ((IClientCommandSource) source).tokyo$getMeta(id);
    }

    static void setMeta(ClientCommandSource source, String id, Object value) {
        ((IClientCommandSource) source).tokyo$setMeta(id, value);
    }
}
