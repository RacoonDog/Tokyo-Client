package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;

@Environment(EnvType.CLIENT)
public interface IVisible {
    boolean tokyo$hasEmojis();

    static boolean hasEmojis(ChatHudLine.Visible line) {
        return ((IVisible) (Object) line).tokyo$hasEmojis();
    }
}
