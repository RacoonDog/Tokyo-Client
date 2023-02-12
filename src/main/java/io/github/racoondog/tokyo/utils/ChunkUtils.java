package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.mixininterface.IPalettedContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public final class ChunkUtils {
    public static BlockState getBlockState(WorldChunk chunk, int x, int y, int z) {
        if (mc.world.isDebugWorld()) return Blocks.AIR.getDefaultState();

        try {
            int l = chunk.getSectionIndex(y);
            if (l >= 0 && l < chunk.getSectionArray().length) {
                ChunkSection chunkSection = chunk.getSection(l);
                if (!chunkSection.isEmpty()) {
                    return chunkSection.getBlockState(x & 15, y & 15, z & 15);
                }
            }
        } catch (Throwable var8) {
            CrashReport crashReport = CrashReport.create(var8, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString(chunk, x, y, z));
            new CrashException(crashReport).printStackTrace();
        }

        return Blocks.AIR.getDefaultState();
    }

    public static List<BlockState> buildBlockStatePalette(ChunkSection section) {
        List<BlockState> list = new ArrayList<>();
        IPalettedContainer.getStorage(section.getBlockStateContainer()).forEach(id -> list.add(IPalettedContainer.getPalette(section.getBlockStateContainer()).get(id)));
        return list;
    }
}
