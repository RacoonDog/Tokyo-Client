package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.systems.accounts.AccessTokenAccount;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = Accounts.class, remap = false)
public abstract class AccountsMixin {
    @Inject(method = "lambda$fromTag$0", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/systems/accounts/AccountType;valueOf(Ljava/lang/String;)Lmeteordevelopment/meteorclient/systems/accounts/AccountType;", shift = At.Shift.AFTER), cancellable = true)
    private static void injectNewMethod(NbtElement tag1, CallbackInfoReturnable<Account<?>> cir) {
        if (((NbtCompound) tag1).getString("type").equals("AccessToken"))
            cir.setReturnValue(new AccessTokenAccount(null).fromTag((NbtCompound) tag1));
    }
}
