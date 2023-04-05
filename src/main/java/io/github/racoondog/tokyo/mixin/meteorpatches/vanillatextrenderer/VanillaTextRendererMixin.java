package io.github.racoondog.tokyo.mixin.meteorpatches.vanillatextrenderer;

import io.github.racoondog.tokyo.mixininterface.IConfig;
import meteordevelopment.meteorclient.renderer.text.VanillaTextRenderer;
import meteordevelopment.meteorclient.systems.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(VanillaTextRenderer.class)
public abstract class VanillaTextRendererMixin {
    @Shadow(remap = false) public double scale;

    @Inject(method = "getWidth", at = @At("RETURN"), cancellable = true, remap = false)
    private void shadowsWidth(String text, int length, boolean shadow, CallbackInfoReturnable<Double> cir) {
        if (!shadow && IConfig.getShadows() && !Config.get().customFont.get()) cir.setReturnValue(cir.getReturnValue() + 1);
    }

    @Inject(method = "getHeight", at = @At("RETURN"), cancellable = true, remap = false)
    private void shadowsHeight(boolean shadow, CallbackInfoReturnable<Double> cir) {
        if (!shadow && IConfig.getShadows() && !Config.get().customFont.get()) cir.setReturnValue(cir.getReturnValue() + 1 * scale);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
    private int useShadows(TextRenderer instance, String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light) {
        return instance.draw(text, x, y, color, IConfig.getShadows(), matrix, vertexConsumers, layerType, backgroundColor, light);
    }
}
