package io.github.racoondog.tokyo.systems;

import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class TokyoStarscript {
    private static final double random = Math.random();

    public static void init() {
        MeteorStarscript.ss.set("tokyo", new ValueMap()
            .set("seeded_random", TokyoStarscript::seededRandom)
        );
    }

    private static Value seededRandom(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3) ss.error("tokyo.seeded_random() requires 2 or 3 arguments, got %d.", argCount);

        double minBound = ss.popNumber("First argument to tokyo.seeded_random() needs to be a number.");
        double maxBound = ss.popNumber("Second argument to tokyo.seeded_random() needs to be a number.");

        double rand = argCount == 2 ? random : Objects.hash(random, ss.pop());

        return Value.number((rand * Math.abs(Math.pow(minBound, maxBound))) % (maxBound - minBound) + minBound);
    }
}
