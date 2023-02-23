package io.github.racoondog.tokyo.mixin.oldchunks;

import io.github.racoondog.tokyo.mixininterface.IPackedIntegerArray;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.PackedIntegerArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(PackedIntegerArray.class)
public abstract class PackedIntegerArrayMixin implements IPackedIntegerArray {
    @Shadow @Final private int elementsPerLong;
    @Shadow @Final private long maxValue;

    @Override
    public int _getElementsPerLong() {
        return this.elementsPerLong;
    }

    @Override
    public long _getMaxValue() {
        return this.maxValue;
    }
}
