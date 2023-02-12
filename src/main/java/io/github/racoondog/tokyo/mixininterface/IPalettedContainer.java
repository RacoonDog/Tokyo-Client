package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

@Environment(EnvType.CLIENT)
public interface IPalettedContainer<T> {
    Palette<T> _getPalette();
    PaletteStorage _getStorage();

    @SuppressWarnings("unchecked")
    static <T> Palette<T> getPalette(PalettedContainer<T> container) {
        return ((IPalettedContainer<T>) container)._getPalette();
    }

    @SuppressWarnings("unchecked")
    static <T> PaletteStorage getStorage(PalettedContainer<T> container) {
        return ((IPalettedContainer<T>) container)._getStorage();
    }
}
