package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Ambience extends Module {
    public static final Ambience INSTANCE = new Ambience();

    private Ambience() {
        super(Tokyo.CATEGORY, "ambience", "");
    }
}
