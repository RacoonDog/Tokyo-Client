package io.github.racoondog.tokyo.mixin.tokyobetterchat;

import io.github.racoondog.tokyo.mixininterface.IMutableText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(MutableText.class)
public class MutableTextMixin implements IMutableText {
    @Shadow @Final @Mutable private TextContent content;

    @Override
    public void setTextContent(TextContent content) {
        this.content = content;
    }
}
