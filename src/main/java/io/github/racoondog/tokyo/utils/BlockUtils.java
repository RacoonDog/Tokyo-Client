package io.github.racoondog.tokyo.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@Environment(EnvType.CLIENT)
public final class BlockUtils {
    public static boolean isAir(Block b) {
        return b == Blocks.AIR || b == Blocks.VOID_AIR || b == Blocks.CAVE_AIR;
    }
}
