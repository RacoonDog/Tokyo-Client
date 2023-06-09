package io.github.racoondog.tokyo.systems;

import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public final class TokyoStarscript {
    private static final Map<Value, Value> VARIABLES = new HashMap<>();

    public static void init() {
        MeteorStarscript.ss.set("player.exhaustion", () -> Value.number(mc.player.getHungerManager().getExhaustion()));
        MeteorStarscript.ss.set("player.hunger", () -> Value.number(mc.player.getHungerManager().getFoodLevel()));
        MeteorStarscript.ss.set("player.saturation", () -> Value.number(mc.player.getHungerManager().getSaturationLevel()));

        MeteorStarscript.ss.set("util._empty", (ss, argCount) -> {
            for (int i = 0; i < argCount; i++) ss.pop();
            return Value.string("");
        });
        MeteorStarscript.ss.set("util._setvar", (ss, argCount) -> {
            Value v = ss.pop();
            VARIABLES.put(ss.pop(), v);
            return v;
        });
        MeteorStarscript.ss.set("util._getvar", (ss, argCount) -> VARIABLES.getOrDefault(ss.pop(), Value.null_()));
        MeteorStarscript.ss.set("util.split", TokyoStarscript::split);
    }

    private static Value split(Starscript ss, int argCount) {
        String string = ss.popString("asbada");
        String regex = ss.popString("bibadoo");

        return wrapList(string.split(regex));
    }

    private static Value wrapList(String[] stringList) {
        return Value.map(new ValueMap()
            .set("get", (ss, argCount) -> Value.string(stringList[(int) ss.popNumber("booboodoo")]))
            .set("length", stringList.length)
            .set("sublist", (ss, argCount) -> {
                int from = (int) ss.popNumber("asd");
                int to = (int) ss.popNumber("oofsa");
                return wrapList(List.of(stringList).subList(from, to).toArray(new String[0]));
            })
        );
    }
}
