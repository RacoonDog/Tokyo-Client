package io.github.racoondog.tokyo.utils.webhook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.utils.network.Http;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WebhookHandler {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static void send(String url, WebhookContent content) {
        JsonObject jsonObject = content.serialize();

        try {
            Http.post(url).bodyJson(GSON.toJson(jsonObject)).send();
        } catch (Throwable t) {
            Tokyo.LOG.error("Could not send webhook: " + t.getMessage());
        }
    }
}
