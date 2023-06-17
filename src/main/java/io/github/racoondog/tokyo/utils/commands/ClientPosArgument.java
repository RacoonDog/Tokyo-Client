package io.github.racoondog.tokyo.utils.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface ClientPosArgument {
    Vec3d toAbsolutePos(CommandSource source);
    Vec2f toAbsoluteRotation(CommandSource source);

    default BlockPos toAbsoluteBlockPos(CommandSource source) {
        return BlockPos.ofFloored(this.toAbsolutePos(source));
    }

    boolean isXRelative();
    boolean isYRelative();
    boolean isZRelative();
}
