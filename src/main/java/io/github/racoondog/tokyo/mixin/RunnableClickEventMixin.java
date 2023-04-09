package io.github.racoondog.tokyo.mixin;

import io.github.racoondog.tokyo.utils.RunnableClickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({Screen.class, BookScreen.class})
public abstract class RunnableClickEventMixin {
    @Inject(method = "handleTextClick", at = @At("TAIL"))
    private void handleTextClick(Style style, CallbackInfoReturnable<Boolean> cir) {
        if (style.getClickEvent() instanceof RunnableClickEvent runnableClickEvent) {
            runnableClickEvent.runnable.run();
        }
    }
}
