package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(PalettedContainer.class)
public interface IPalettedContainer {
    @Accessor("data")
    <T> PalettedContainer.Data<T> tokyo$getData();

    @Accessor("paletteProvider")
    PalettedContainer.PaletteProvider tokyo$getPaletteProvider();
}
