package io.github.racoondog.tokyo.mixin.chatemojis;

import io.github.racoondog.tokyo.mixininterface.IVisible;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(ChatHudLine.Visible.class)
public abstract class VisibleMixin implements IVisible {
    @Unique private boolean hasEmojis = false;

    @Override
    public boolean tokyo$hasEmojis() {
        return this.hasEmojis;
    }
}
