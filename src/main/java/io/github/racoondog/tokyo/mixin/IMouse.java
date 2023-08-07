package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public interface IMouse {
    @Invoker("onMouseButton")
    void tokyo$invokeOnMouseButton(long window, int button, int action, int mods);

    @Invoker("onMouseScroll")
    void tokyo$invokeOnMouseScroll(long window, double horizontal, double vertical);

    @Invoker("onCursorPos")
    void tokyo$invokeOnCursorPos(long window, double x, double y);
}
