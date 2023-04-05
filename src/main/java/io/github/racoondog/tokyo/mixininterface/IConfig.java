package io.github.racoondog.tokyo.mixininterface;

import io.github.racoondog.tokyo.utils.MinecraftFont;
import meteordevelopment.meteorclient.systems.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IConfig {
    boolean tokyo$getShadows();
    MinecraftFont tokyo$getMinecraftFont();

    static boolean getShadows() {
        return ((IConfig) Config.get()).tokyo$getShadows();
    }

    static MinecraftFont getMinecraftFont() {
        return ((IConfig) Config.get()).tokyo$getMinecraftFont();
    }
}
