package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.utils.RecolorGuiTheme;
import meteordevelopment.meteorclient.gui.GuiTheme;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(value = GuiTheme.class, remap = false)
public abstract class GuiThemeMixin {
    @Shadow @Final @Mutable public String name;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lmeteordevelopment/meteorclient/gui/GuiTheme;name:Ljava/lang/String;"))
    private void rename(GuiTheme instance, String value) {
        if (instance instanceof RecolorGuiTheme recolorGuiTheme) name = recolorGuiTheme.getName();
        else name = value;
    }
}
