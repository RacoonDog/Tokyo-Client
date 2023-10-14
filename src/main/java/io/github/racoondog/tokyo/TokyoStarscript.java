package io.github.racoondog.tokyo;

import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public final class TokyoStarscript {
    private static final Map<Value, Value> VARIABLES = new HashMap<>();

    public static void init() {
        MeteorStarscript.ss.set("player.exhaustion", () -> Value.number(mc.player != null ? mc.player.getHungerManager().getExhaustion() : 0));
        MeteorStarscript.ss.set("player.hunger", () -> Value.number(mc.player != null ? mc.player.getHungerManager().getFoodLevel() : 0));
        MeteorStarscript.ss.set("player.saturation", () -> Value.number(mc.player != null ? mc.player.getHungerManager().getSaturationLevel() : 0));

        MeteorStarscript.ss.set("util._empty", (ss, argCount) -> {
            for (int i = 0; i < argCount; i++) ss.pop();
            return Value.string("");
        });
        MeteorStarscript.ss.set("util._setvar", (ss, argCount) -> {
            if (argCount != 2) ss.error("util._setvar requires 2 arguments, got %d.", argCount);
            Value v = ss.pop();
            VARIABLES.put(ss.pop(), v);
            return v;
        });
        MeteorStarscript.ss.set("util._getvar", (ss, argCount) -> {
            if (argCount != 1) ss.error("util._getvar requires 1 argument, got %d.", argCount);
            return VARIABLES.getOrDefault(ss.pop(), Value.null_());
        });
        MeteorStarscript.ss.set("util.split", TokyoStarscript::split);
    }

    private static Value split(Starscript ss, int argCount) {
        if (argCount != 2) ss.error("util.split() requires 2 arguments, got %d.", argCount);
        String string = ss.popString("First argument to split() needs to be a string.");
        String regex = ss.popString("Second argument to split() needs to be a string.");

        return wrapList(string.split(regex));
    }

    private static Value wrapList(String[] stringList) {
        return Value.map(new ValueMap()
            .set("get", (ss, argCount) -> Value.string(stringList[(int) ss.popNumber("Argument to get() needs to be a number.")]))
            .set("length", stringList.length)
            .set("sublist", (ss, argCount) -> {
                int from = (int) ss.popNumber("First argument to sublist() needs to be a number.");
                int to = (int) ss.popNumber("Second argument to sublist() needs to be a number.");
                return wrapList(List.of(stringList).subList(from, to).toArray(new String[0]));
            })
        );
    }
}
