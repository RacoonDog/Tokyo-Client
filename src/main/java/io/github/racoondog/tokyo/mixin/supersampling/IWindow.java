package io.github.racoondog.tokyo.mixin.supersampling;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Window.class)
public interface IWindow {
    @Invoker("onWindowSizeChanged")
    void updateWindowSize(long window, int width, int height);
}
