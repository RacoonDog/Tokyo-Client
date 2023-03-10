package io.github.racoondog.tokyo.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;

@Environment(EnvType.CLIENT)
public interface IMutableText {
    void tokyo$setTextContent(TextContent content);

    static void setTextContent(MutableText text, TextContent content) {
        ((IMutableText) text).tokyo$setTextContent(content);
    }
}
