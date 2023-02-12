package io.github.racoondog.tokyo.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Math;

@Environment(EnvType.CLIENT)
public final class MathUtils {
    public static double distance(double deltaX, double deltaZ) {
        return Math.sqrt(Math.fma(deltaX, deltaX, deltaZ * deltaZ));
    }
}
