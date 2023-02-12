package io.github.racoondog.tokyo.auth;

import io.github.racoondog.tokyo.utils.FileUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Loader {
    public static void init() {
        //Get HWID choice cache
        FileUtils.getLines("null");
    }
}
