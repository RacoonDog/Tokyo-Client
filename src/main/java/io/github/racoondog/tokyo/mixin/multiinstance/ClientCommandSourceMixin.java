package io.github.racoondog.tokyo.mixin.multiinstance;

import io.github.racoondog.tokyo.mixininterface.IClientCommandSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ClientCommandSource.class)
public abstract class ClientCommandSourceMixin implements IClientCommandSource {
    @Unique private final Map<String, Object> meta = new HashMap<>();

    @Override
    public Object tokyo$getMeta(String id) {
        return meta.get(id);
    }

    @Override
    public void tokyo$setMeta(String id, Object value) {
        meta.put(id, value);
    }
}
