package io.github.racoondog.tokyo.utils;

import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public final class AccountUtil {
    @Nullable
    public static Account<?> getSelectedAccount() {
        String name = mc.getSession().getUsername();
        for (var acc : Accounts.get()) {
            if (name.equals(acc.getUsername())) return acc;
        }
        return null;
    }
}
