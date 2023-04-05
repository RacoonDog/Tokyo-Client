package io.github.racoondog.tokyo.utils.misc;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface JsonSerializable {
    JsonObject serialize();
}
