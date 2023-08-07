package io.github.racoondog.tokyo.mixin;

import io.github.racoondog.tokyo.event.MouseButtonEvent;
import io.github.racoondog.tokyo.event.MouseMoveEvent;
import io.github.racoondog.tokyo.event.MouseScrollEvent;
import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void injectMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

        MeteorClient.EVENT_BUS.post(MouseButtonEvent.get(button, action, mods));
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void injectMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

        MeteorClient.EVENT_BUS.post(MouseScrollEvent.get(horizontal, vertical));
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void injectCursorPos(long window, double x, double y, CallbackInfo ci) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

        MeteorClient.EVENT_BUS.post(MouseMoveEvent.get(x, y));
    }
}
