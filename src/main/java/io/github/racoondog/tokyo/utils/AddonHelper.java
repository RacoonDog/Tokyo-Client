package io.github.racoondog.tokyo.utils;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.utils.PreInit;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Optional;

public class AddonHelper {
    private static final Reference2ObjectMap<MeteorAddon, ModContainer> ADDON_METADATA_MAP = new Reference2ObjectOpenHashMap<>();

    @PreInit
    public static void preInit() {
        for (var entrypoint : FabricLoader.getInstance().getEntrypointContainers("meteor", MeteorAddon.class)) {
            ADDON_METADATA_MAP.put(entrypoint.getEntrypoint(), entrypoint.getProvider());
        }
        ADDON_METADATA_MAP.put(MeteorClient.ADDON, FabricLoader.getInstance().getModContainer("meteor-client").orElseThrow(() -> new IllegalStateException("Meteor mod container not found!")));
    }

    public static Optional<ModContainer> getContainer(MeteorAddon addon) {
        return Optional.ofNullable(ADDON_METADATA_MAP.get(addon));
    }

    public static Optional<ModMetadata> getMeta(MeteorAddon addon) {
        return getContainer(addon).map(ModContainer::getMetadata);
    }
}
