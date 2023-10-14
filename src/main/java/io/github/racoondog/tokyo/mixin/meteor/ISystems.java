package io.github.racoondog.tokyo.mixin.meteor;

import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(value = Systems.class, remap = false)
public interface ISystems {
    @Invoker("add")
    static System<?> tokyo$invokeAdd(System<?> system) {
        throw new AssertionError();
    }
}
