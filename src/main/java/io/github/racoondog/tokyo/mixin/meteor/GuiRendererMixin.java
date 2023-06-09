package io.github.racoondog.tokyo.mixin.meteor;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.systems.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(value = GuiRenderer.class, remap = false)
public abstract class GuiRendererMixin {
    @ModifyArg(method = "endRender", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/GuiTheme;scale(D)D", ordinal = 1))
    private double resetScale(double value) {
        if (!Config.get().customFont.get()) return 1;
        return value;
    }
}
