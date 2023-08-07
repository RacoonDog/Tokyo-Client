package io.github.racoondog.tokyo.mixin;

import io.github.racoondog.tokyo.event.CharTypedEvent;
import io.github.racoondog.tokyo.event.KeyPressEvent;
import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void injectKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

        MeteorClient.EVENT_BUS.post(KeyPressEvent.get(key, scancode, action, modifiers));
    }

    @Inject(method = "onChar", at = @At("HEAD"))
    private void injectChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

        MeteorClient.EVENT_BUS.post(CharTypedEvent.get(codePoint, modifiers));
    }
}
