package io.github.racoondog.tokyo.mixin.export.logoutspots;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(targets = "meteordevelopment.meteorclient.systems.modules.render.LogoutSpots$Entry", remap = false)
public interface ILogoutSpotsEntry {
    @Accessor("x") double tokyo$getX();
    @Accessor("y") double tokyo$getY();
    @Accessor("z") double tokyo$getZ();
    @Accessor("uuid") UUID tokyo$getUuid();
    @Accessor("name") String tokyo$getName();
}
