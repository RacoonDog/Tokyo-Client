package io.github.racoondog.tokyo.mixin.meteor;

import meteordevelopment.meteorclient.systems.accounts.types.MicrosoftAccount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(value = MicrosoftAccount.class, remap = false)
public interface IMicrosoftAccount {
    @Invoker("auth")
    String tokyo$invokeAuth();
}
