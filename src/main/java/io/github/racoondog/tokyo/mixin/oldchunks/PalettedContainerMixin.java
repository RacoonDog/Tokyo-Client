package io.github.racoondog.tokyo.mixin.oldchunks;

import io.github.racoondog.tokyo.mixininterface.IPalettedContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(PalettedContainer.class)
public abstract class PalettedContainerMixin<T> implements IPalettedContainer<T> {
    @Shadow private volatile PalettedContainer.Data<T> data;

    @Override
    public Palette<T> _getPalette() {
        return this.data.palette();
    }

    @Override
    public PaletteStorage _getStorage() {
        return this.data.storage();
    }
}
