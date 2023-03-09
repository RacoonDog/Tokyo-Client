package io.github.racoondog.tokyo.mixin.supersampling;

import io.github.racoondog.tokyo.systems.modules.SuperSampling;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(value = Mesh.class, remap = false)
public abstract class MeshMixin {
    @ModifyVariable(method = "vec2", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double modifyWidth(double originalWidth) {
        Mesh self = (Mesh) (Object) this;
        if (SuperSampling.INSTANCE.isActive() && self == Renderer2D.COLOR.lines || self == Renderer2D.COLOR.triangles || self == Renderer2D.TEXTURE.lines || self == Renderer2D.TEXTURE.triangles) {
            return originalWidth * SuperSampling.INSTANCE.sampleMultiplier;
        }
        return originalWidth;
    }

    @ModifyVariable(method = "vec2", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double modifyHeight(double originalHeight) {
        Mesh self = (Mesh) (Object) this;
        if (SuperSampling.INSTANCE.isActive() && self == Renderer2D.COLOR.lines || self == Renderer2D.COLOR.triangles || self == Renderer2D.TEXTURE.lines || self == Renderer2D.TEXTURE.triangles) {
            return originalHeight * SuperSampling.INSTANCE.sampleMultiplier;
        }
        return originalHeight;
    }
}
