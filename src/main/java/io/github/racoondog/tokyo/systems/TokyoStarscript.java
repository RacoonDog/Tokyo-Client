package io.github.racoondog.tokyo.systems;

import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.value.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public final class TokyoStarscript {
    public static void init() {
        MeteorStarscript.ss.set("player.exhaustion", () -> Value.number(mc.player.getHungerManager().getExhaustion()));
        MeteorStarscript.ss.set("player.hunger", () -> Value.number(mc.player.getHungerManager().getFoodLevel()));
        MeteorStarscript.ss.set("player.saturation", () -> Value.number(mc.player.getHungerManager().getSaturationLevel()));
    }
}
