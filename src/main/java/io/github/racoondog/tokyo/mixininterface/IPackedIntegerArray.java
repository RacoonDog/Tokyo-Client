package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.PaletteStorage;

@Environment(EnvType.CLIENT)
public interface IPackedIntegerArray {
    int _getElementsPerLong();
    long _getMaxValue();

    static int getElementsPerLong(PaletteStorage storage) {
        return ((IPackedIntegerArray) storage)._getElementsPerLong();
    }

    static long getMaxValue(PaletteStorage storage) {
        return ((IPackedIntegerArray) storage)._getMaxValue();
    }
}
