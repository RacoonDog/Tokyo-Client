package io.github.racoondog.tokyo.utils.webhook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.racoondog.tokyo.utils.JsonSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WebhookContent implements JsonSerializable {
    private final String content;
    private final String username;
    private final String avatarUrl;
    private final boolean tts;
    private final List<EmbedObject> embeds = new ArrayList<>();

    public WebhookContent(String content, String username, String avatarUrl, boolean tts) {
        this.content = content;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.tts = tts;
    }

    public void addEmbed(EmbedObject embedObject) {
        embeds.add(embedObject);
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("content", content);
        object.addProperty("username", username);
        object.addProperty("avatar_url", avatarUrl);
        object.addProperty("tts", "false");

        if (!embeds.isEmpty()) {
            JsonArray embedList = new JsonArray();
            for (var embed : embeds) embedList.add(embed.serialize());
            object.add("embeds", embedList);
        }

        return object;
    }
}
