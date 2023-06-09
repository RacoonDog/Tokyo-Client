package io.github.racoondog.tokyo.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

/**
 * Used for getting raw nullable variables from {@link Style}
 */
@Environment(EnvType.CLIENT)
@Mixin(Style.class)
public interface IStyle {
    @Accessor("color") @Nullable TextColor tokyo$getColor();
    @Accessor("bold") @Nullable Boolean tokyo$getBold();
    @Accessor("italic") @Nullable Boolean tokyo$getItalic();
    @Accessor("underlined") @Nullable Boolean tokyo$getUnderlined();
    @Accessor("strikethrough") @Nullable Boolean tokyo$getStrikethrough();
    @Accessor("obfuscated") @Nullable Boolean tokyo$getObfuscated();
    @Accessor("clickEvent") @Nullable ClickEvent tokyo$getClickEvent();
    @Accessor("hoverEvent") @Nullable HoverEvent tokyo$getHoverEvent();
    @Accessor("insertion") @Nullable String tokyo$getInsertion();
    @Accessor("font") @Nullable Identifier tokyo$getFont();
}
