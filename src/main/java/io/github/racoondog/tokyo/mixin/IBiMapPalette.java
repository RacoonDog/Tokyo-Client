package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.world.chunk.BiMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(BiMapPalette.class)
public interface IBiMapPalette<T> {
    @Accessor("idList")
    IndexedIterable<T> tokyo$getIdList();

    @Accessor("map")
    Int2ObjectBiMap<T> tokyo$getMap();
}
