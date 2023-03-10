package io.github.racoondog.tokyo.mixininterface;

import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.Script;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IStarscript {
    String tokyo$run_rawOutput(Script script, StringBuilder sb);

    static String run_rawOutput(Script script, StringBuilder sb) {
        return ((IStarscript) MeteorStarscript.ss).tokyo$run_rawOutput(script, sb);
    }

    static String run_rawOutput(Script script) {
        return run_rawOutput(script, new StringBuilder());
    }
}
