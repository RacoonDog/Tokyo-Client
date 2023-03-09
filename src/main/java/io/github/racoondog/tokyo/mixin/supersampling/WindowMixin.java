package io.github.racoondog.tokyo.mixin.supersampling;

import io.github.racoondog.tokyo.systems.modules.SuperSampling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(Window.class)
public abstract class WindowMixin {
    @ModifyVariable(method = "onFramebufferSizeChanged", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int modifyWidth(int originalWidth) {
        if (SuperSampling.INSTANCE.isActive()) {
            SuperSampling.INSTANCE.supersampled = true;
            return (int) (originalWidth * SuperSampling.INSTANCE.sampleMultiplier);
        }
        return originalWidth;
    }

    @ModifyVariable(method = "onFramebufferSizeChanged", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private int modifyHeight(int originalHeight) {
        if (SuperSampling.INSTANCE.isActive()) {
            return (int) (originalHeight * SuperSampling.INSTANCE.sampleMultiplier);
        }
        return originalHeight;
    }

    @Redirect(method = "updateFramebufferSize", at = @At(value = "INVOKE", target = "org/lwjgl/glfw/GLFW.glfwGetFramebufferSize(J[I[I)V", remap = false))
    private void modifyInitialBufferSize(long window, int[] width, int[] height) {
        if (SuperSampling.INSTANCE.isActive()) {
            SuperSampling.INSTANCE.supersampled = true;
            GLFW.glfwGetFramebufferSize(window, width, height);
            width[0] *= SuperSampling.INSTANCE.sampleMultiplier;
            height[0] *= SuperSampling.INSTANCE.sampleMultiplier;
            SuperSampling.INSTANCE.updateBuffer();
        } else SuperSampling.INSTANCE.supersampled = false;
    }
}
