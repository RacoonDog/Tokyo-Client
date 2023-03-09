package io.github.racoondog.tokyo.mixin.supersampling;

import io.github.racoondog.tokyo.systems.modules.SuperSampling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
@Mixin(ScreenshotRecorder.class)
public abstract class ScreenshotRecorderMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "takeScreenshot", at = @At("HEAD"), cancellable = true, require = 0)
    private static void injectScreenshot(Framebuffer framebuffer, CallbackInfoReturnable<NativeImage> cir) {
        if (SuperSampling.INSTANCE.isActive() && framebuffer == mc.getFramebuffer()) {
            int width = framebuffer.textureWidth;
            int height = framebuffer.textureHeight;

            ByteBuffer data = MemoryUtil.memAlloc((int) (width * height * SuperSampling.INSTANCE.sampleMultiplierSquared));

            try {
                framebuffer.beginRead();
                GL33.glGetTexImage(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, data);
                for (int i = 0; i < data.limit(); i += 4) data.put(i + 3, (byte) 0xFF);
                var img = new NativeImage((int) (width / SuperSampling.INSTANCE.sampleMultiplier), (int) (height / SuperSampling.INSTANCE.sampleMultiplier), false);
                var odata = MemoryUtil.memByteBuffer(((INativeImage)(Object)img).tokyo$getPointer(), (int) (width * height * SuperSampling.INSTANCE.sampleMultiplier));
                STBImageResize.stbir_resize_uint8_generic(data, width, height, 0,
                    odata, (int) (width / SuperSampling.INSTANCE.sampleMultiplier), (int) (height / SuperSampling.INSTANCE.sampleMultiplier), 0,
                    4, 0, 3, STBImageResize.STBIR_EDGE_CLAMP,
                    STBImageResize.STBIR_FILTER_BOX, STBImageResize.STBIR_COLORSPACE_LINEAR);
                img.mirrorVertically();
                cir.setReturnValue(img);
            } finally {
                MemoryUtil.memFree(data);
            }
        }
    }
}
