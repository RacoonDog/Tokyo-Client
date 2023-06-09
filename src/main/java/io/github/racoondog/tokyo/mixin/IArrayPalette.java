package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.ArrayPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ArrayPalette.class)
public interface IArrayPalette<T> {
    @Accessor("idList")
    IndexedIterable<T> tokyo$getIdList();

    @Accessor("array")
    T[] tokyo$getArray();
}
