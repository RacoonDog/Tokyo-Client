package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.LaunchHandler;
import meteordevelopment.meteorclient.systems.Systems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = Systems.class, remap = false)
public abstract class SystemsMixin {
    @Inject(method = "save(Ljava/io/File;)V", at = @At("HEAD"), cancellable = true)
    private static void injectSave(CallbackInfo ci) {
        if (LaunchHandler.freezeSettings) ci.cancel();
    }
}
