package io.github.racoondog.tokyo;

import io.github.racoondog.tokyo.systems.modules.ChatEmojis;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChatEmojis.earlyInit();
    }
}
