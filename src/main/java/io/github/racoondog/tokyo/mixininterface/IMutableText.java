package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextContent;

@Environment(EnvType.CLIENT)
public interface IMutableText {
    void setTextContent(TextContent content);
}
