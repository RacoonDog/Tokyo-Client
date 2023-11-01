package io.github.racoondog.tokyo.systems.seedresolver;

import com.google.gson.JsonArray;
import io.github.racoondog.tokyo.mixin.IHttpRequest;
import io.github.racoondog.tokyo.mixininterface.IClientCommandSource;
import io.github.racoondog.tokyo.systems.config.TokyoConfig;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SeedResolver extends System<SeedResolver> {
    public static final SeedResolver INSTANCE = new SeedResolver();
    @Nullable
    private Long seed = null;
    private final Object2LongMap<String> configSeeds = new Object2LongOpenHashMap<>();
    private final Object2LongMap<String> databaseSeeds = new Object2LongOpenHashMap<>();

    public SeedResolver() {
        super("seed-resolver");

        MeteorExecutor.execute(() -> {
            Http.Request request = Http.get("https://script.google.com/macros/s/AKfycbxvJa3GPh2B_atqbxFdlpInPw4XGKk1jR7lHqALx1durf0X-VXi6bG4zi7Jg-FCU3DfFg/exec");
            ((IHttpRequest) request).tokyo$getBuilder().timeout(Duration.ofSeconds(30));
            JsonArray databaseSeedArray = request.sendJson(JsonArray.class);
            if (databaseSeedArray == null) return;
            for (var databaseEntry : databaseSeedArray) {
                JsonArray seedEntry = databaseEntry.getAsJsonArray();
                String address = seedEntry.get(0).getAsString();
                String seedString = seedEntry.get(2).getAsString();
                long seed = Long.parseLong(seedString.substring(0, seedString.length() - 1));
                databaseSeeds.put(address, seed);
            }
        });

        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        seed = null;
    }

    @Nullable
    public Long getSeed() {
        for (var method : TokyoConfig.INSTANCE.seedResolutionMethods.get()) {
            switch (method) {
                case SeedConfig -> {
                    if (seed != null) return seed;
                }
                case SavedSeedConfigs -> {
                    Long connectedSeed = fromConnection();
                    if (connectedSeed != null) return connectedSeed;
                }
                case OnlineDatabase -> {
                    Long databaseSeed = fromDatabase();
                    if (databaseSeed != null) return databaseSeed;
                }
            }
        }
        return null;
    }

    @Nullable
    public Long getSeed(CommandSource source) {
        for (var method : TokyoConfig.INSTANCE.seedResolutionMethods.get()) {
            switch (method) {
                case SeedConfig -> {
                    if (seed != null) return seed;
                }
                case SavedSeedConfigs -> {
                    Long connectedSeed = fromConnection();
                    if (connectedSeed != null) return connectedSeed;
                }
                case OnlineDatabase -> {
                    Long databaseSeed = fromDatabase();
                    if (databaseSeed != null) return databaseSeed;
                }
                case CommandSource -> {
                    Long commandSourceSeed = (Long) IClientCommandSource.getMeta((ClientCommandSource) source, "tokyo$seed");
                    if (commandSourceSeed != null) return  commandSourceSeed;
                }
            }
        }
        return null;
    }

    @Nullable
    private Long fromConnection() {
        if (mc.isIntegratedServerRunning()) {
            return mc.getServer().getOverworld().getSeed();
        }

        if (mc.getNetworkHandler().isConnectionOpen()) {
            String connection = mc.getNetworkHandler().getConnection().getAddress().toString();
            if (configSeeds.containsKey(connection)) return configSeeds.getLong(connection);
        }

        return null;
    }

    @Nullable
    private Long fromDatabase() {
        if (mc.getNetworkHandler().isConnectionOpen()) {
            String connection = mc.getNetworkHandler().getConnection().getAddress().toString();
            if (databaseSeeds.containsKey(connection)) return databaseSeeds.getLong(connection);
        }

        return null;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound compound = new NbtCompound();

        for (var seedEntry : configSeeds.object2LongEntrySet()) {
            compound.putLong(seedEntry.getKey(), seedEntry.getLongValue());
        }

        return compound;
    }

    @Override
    public SeedResolver fromTag(NbtCompound tag) {
        for (var seedEntry : tag.getKeys()) {
            configSeeds.put(seedEntry, tag.getLong(seedEntry));
        }

        return this;
    }

    public enum ResolutionMethod {
        CommandSource,
        SavedSeedConfigs,
        OnlineDatabase,
        SeedConfig
    }
}
