package io.github.racoondog.tokyo.mixin.supersampling;

import io.github.racoondog.tokyo.systems.modules.SuperSampling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL33;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
@Mixin(Framebuffer.class)
public abstract class FramebufferMixin {
    @SuppressWarnings("ConstantConditions")
    @ModifyConstant(method = "initFbo", constant = @Constant(intValue = GL33.GL_NEAREST))
    private int linearFiltering(int constant) {
        if (SuperSampling.INSTANCE.isActive() && (Object) this == mc.getFramebuffer()) {
            return GL33.GL_LINEAR;
        }
        return constant;
    }

    @Inject(method = "drawInternal", at = @At("HEAD"), cancellable = true)
    private void injectDraw(int width, int height, boolean disableBlend, CallbackInfo ci) {
        Framebuffer self = (Framebuffer) (Object) this;
        if (SuperSampling.INSTANCE.isActive() && self == mc.getFramebuffer()) {
            GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, self.fbo);
            GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, 0);
            GL33.glBlitFramebuffer(0, 0, width, height, 0, 0, (int) (width / SuperSampling.INSTANCE.sampleMultiplier), (int) (height / SuperSampling.INSTANCE.sampleMultiplier), GL33.GL_COLOR_BUFFER_BIT, GL33.GL_LINEAR);
            ci.cancel();
        }
    }
}
