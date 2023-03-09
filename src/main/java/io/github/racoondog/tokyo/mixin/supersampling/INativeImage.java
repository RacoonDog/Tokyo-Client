package io.github.racoondog.tokyo.mixin.supersampling;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(NativeImage.class)
public interface INativeImage {

    @Accessor("pointer")
    long tokyo$getPointer();

}
