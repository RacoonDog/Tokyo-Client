package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
public interface IKeyboard {
    @Invoker("onChar")
    void tokyo$invokeOnChar(long window, int codePoint, int modfiers);
}
