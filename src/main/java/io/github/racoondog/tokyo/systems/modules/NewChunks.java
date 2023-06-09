package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class NewChunks extends Module {
    public static final NewChunks INSTANCE = new NewChunks();

    private NewChunks() {
        super(Tokyo.CATEGORY, "new-chunks", "");
    }
}
