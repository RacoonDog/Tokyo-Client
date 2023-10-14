package io.github.racoondog.tokyo.systems.accounts;

import com.mojang.util.UndashedUuid;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.types.MicrosoftAccount;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.client.session.Session;

import java.util.Optional;

public class AccessTokenAccount extends Account<AccessTokenAccount> {
    public AccessTokenAccount(String token) {
        super(AccountTypeExtender.AccessToken, token);
    }

    @Override
    public boolean fetchInfo() {
        return auth();
    }

    @Override
    public boolean login() {
        super.login();

        if (!auth()) return false;

        cache.loadHead();

        setSession(new Session(cache.username, UndashedUuid.fromStringLenient(cache.uuid), name, Optional.empty(), Optional.empty(), Session.AccountType.MSA));
        return true;
    }

    private boolean auth() {
        // Check game ownership
        GameOwnershipResponse gameOwnershipRes = Http.get("https://api.minecraftservices.com/entitlements/mcstore")
            .bearer(name)
            .sendJson(GameOwnershipResponse.class);

        if (gameOwnershipRes == null || !gameOwnershipRes.hasGameOwnership()) return false;

        // Profile
        ProfileResponse profileRes = Http.get("https://api.minecraftservices.com/minecraft/profile")
            .bearer(name)
            .sendJson(ProfileResponse.class);

        if (profileRes == null) return false;

        cache.username = profileRes.name;
        cache.uuid = profileRes.id;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MicrosoftAccount || o instanceof AccessTokenAccount) && ((Account<?>) o).getUsername().equals(getUsername());
    }

    private static class GameOwnershipResponse {
        private GameOwnershipResponse.Item[] items;

        private static class Item {
            private String name;
        }

        private boolean hasGameOwnership() {
            boolean hasProduct = false;
            boolean hasGame = false;

            for (GameOwnershipResponse.Item item : items) {
                if (item.name.equals("product_minecraft")) hasProduct = true;
                else if (item.name.equals("game_minecraft")) hasGame = true;
            }

            return hasProduct && hasGame;
        }
    }

    private static class ProfileResponse {
        public String id;
        public String name;
    }
}
