package io.github.racoondog.tokyo.mixin.meteor;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.Swarm;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(value = Swarm.class, remap = false)
public interface ISwarm {
    @Accessor("ipAddress")
    Setting<String> tokyo$getIpAddress();

    @Accessor("serverPort")
    Setting<Integer> tokyo$getServerPort();
}
