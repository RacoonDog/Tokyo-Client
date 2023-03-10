package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

@Environment(EnvType.CLIENT)
public interface IPalettedContainer<T> {
    Palette<T> tokyo$getPalette();
    PaletteStorage tokyo$getStorage();

    @SuppressWarnings("unchecked")
    static <T> Palette<T> getPalette(PalettedContainer<T> container) {
        return ((IPalettedContainer<T>) container).tokyo$getPalette();
    }

    @SuppressWarnings("unchecked")
    static <T> PaletteStorage getStorage(PalettedContainer<T> container) {
        return ((IPalettedContainer<T>) container).tokyo$getStorage();
    }
}
