package io.github.racoondog.tokyo.mixin.meteorpatches.vanillatextrenderer;

import io.github.racoondog.tokyo.mixininterface.IConfig;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderOperation;
import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import meteordevelopment.meteorclient.systems.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
@Mixin(value = TextOperation.class, remap = false)
public abstract class TextOperationMixin extends GuiRenderOperation<TextOperation> {
    @Shadow public boolean title;
    @Shadow private String text;

    @Inject(method = "onRun", at = @At("HEAD"), cancellable = true)
    private void injectHead(CallbackInfo ci) {
        if (!Config.get().customFont.get()) {
            MatrixStack stack = new MatrixStack();
            stack.scale(2, 2, 1);

            Style style = Style.EMPTY.withFont(IConfig.getMinecraftFont().get());
            if (title) style = style.withBold(true);

            if (IConfig.getShadows()) mc.textRenderer.drawWithShadow(stack, Text.literal(text).setStyle(style), (float) (x + 1) / 2, title ? (float) (y + 4) / 2 : (float) (y + 2) / 2, color.getPacked());
            else mc.textRenderer.draw(stack, Text.literal(text).setStyle(style), (float) (x + 1) / 2, title ? (float) (y + 4) / 2 : (float) (y + 2) / 2, color.getPacked());

            ci.cancel();
        }
    }
}
