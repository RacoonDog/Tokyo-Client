package io.github.racoondog.tokyo.utils;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class VerboseUtils {
    private static final Int2LongMap tickingLogMap = new Int2LongOpenHashMap();

    @PostInit
    public static void postInit() {
        MeteorClient.EVENT_BUS.subscribe(VerboseUtils.class);
    }

    @EventHandler
    private static void onTick(TickEvent.Post tickEvent) {
        long currentEpoch = System.currentTimeMillis();
        for (var entry : tickingLogMap.int2LongEntrySet()) {
            if (entry.getLongValue() + 1000 <= currentEpoch) tickingLogMap.remove(entry.getIntKey());
        }
    }

    public static void warnInLoop(String message) {
        int hash = message.hashCode();
        if (!tickingLogMap.containsKey(hash)) {
            tickingLogMap.put(hash, System.currentTimeMillis());
            ChatUtils.warning(message);
        }
    }
}
