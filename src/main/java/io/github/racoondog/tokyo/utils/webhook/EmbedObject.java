package io.github.racoondog.tokyo.utils.webhook;

import com.google.gson.JsonObject;
import io.github.racoondog.tokyo.utils.misc.JsonSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmbedObject implements JsonSerializable {
    @Override
    public JsonObject serialize() {
        return new JsonObject();
    }
}
