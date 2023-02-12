package io.github.racoondog.tokyo.utils.prefix;

import io.github.racoondog.tokyo.mixin.prefix.IStyle;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RainbowStyle extends Style implements ISerializable<RainbowStyle> {
    private final Style inner;
    private final RainbowColor color;
    private final int offset;

    private RainbowStyle(Style inner, RainbowColor color, int offset) {
        //Should not be required, but is there anyway
        super(((IStyle) inner).tokyo$getColor(), ((IStyle) inner).tokyo$getBold(), ((IStyle) inner).tokyo$getItalic(), ((IStyle) inner).tokyo$getUnderlined(), ((IStyle) inner).tokyo$getStrikethrough(), ((IStyle) inner).tokyo$getObfuscated(), ((IStyle) inner).tokyo$getClickEvent(), ((IStyle) inner).tokyo$getHoverEvent(), ((IStyle) inner).tokyo$getInsertion(), ((IStyle) inner).tokyo$getFont());
        this.inner = inner;
        this.color = color;
        this.offset = offset;
    }

    public RainbowStyle(Style inner, RainbowColor color) {
        this(inner, color, 0);
    }



    public static Text fade(String text, Style parent, RainbowColor color) {
        return Text.literal(text).setStyle(new RainbowStyle(parent, color));
    }

    public static Text fadeOffset(String text, Style parent, RainbowColor color) {
        if (color.horizontalOffset <= 0.0d) return fade(text, parent, color);

        MutableText root = Text.empty().setStyle(parent);
        Text[] fade = generateFade(text, parent, color);

        for (var value : fade) {
            root.append(value);
        }

        return root;
    }

    public static Text[] generateFade(String text, Style parent, RainbowColor color) {
        String[] chars = text.split("");
        Text[] output = new Text[chars.length];

        for (int i = 0; i < chars.length; i++) {
            String c = chars[i];
            RainbowStyle style = new RainbowStyle(parent, color, i);
            output[i] = Text.literal(c).setStyle(style);
        }

        return output;
    }

    /**
     * Use the color from {@link RainbowColor}
     */
    @Nullable
    @Override
    public TextColor getColor() {
        return TextColor.fromRgb(color.getColor(offset));
    }

    /** {@link Style} wrapper methods */

    @Override
    public boolean isBold() {
        return inner.isBold();
    }

    @Override
    public boolean isItalic() {
        return inner.isItalic();
    }

    @Override
    public boolean isStrikethrough() {
        return inner.isStrikethrough();
    }

    @Override
    public boolean isUnderlined() {
        return inner.isUnderlined();
    }

    @Override
    public boolean isObfuscated() {
        return inner.isObfuscated();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Nullable
    @Override
    public ClickEvent getClickEvent() {
        return inner.getClickEvent();
    }

    @Nullable
    @Override
    public HoverEvent getHoverEvent() {
        return inner.getHoverEvent();
    }

    @Nullable
    @Override
    public String getInsertion() {
        return inner.getInsertion();
    }

    @Override
    public Identifier getFont() {
        return inner.getFont();
    }

    @Override
    public Style withParent(Style parent) {
        return new RainbowStyle(inner.withParent(parent), color, offset);
    }

    @Override
    public Style withColor(@Nullable TextColor color) {
        return new RainbowStyle(inner.withColor(color), this.color, offset);
    }

    @Override
    public Style withColor(@Nullable Formatting color) {
        return new RainbowStyle(inner.withColor(color), this.color, offset);
    }

    @Override
    public Style withColor(int rgbColor) {
        return new RainbowStyle(inner.withColor(rgbColor), color, offset);
    }

    @Override
    public Style withBold(@Nullable Boolean bold) {
        return new RainbowStyle(inner.withBold(bold), color, offset);
    }

    @Override
    public Style withItalic(@Nullable Boolean italic) {
        return new RainbowStyle(inner.withItalic(italic), color, offset);
    }

    @Override
    public Style withUnderline(@Nullable Boolean underline) {
        return new RainbowStyle(inner.withUnderline(underline), color, offset);
    }

    @Override
    public Style withStrikethrough(@Nullable Boolean strikethrough) {
        return new RainbowStyle(inner.withStrikethrough(strikethrough), color, offset);
    }

    @Override
    public Style withObfuscated(@Nullable Boolean obfuscated) {
        return new RainbowStyle(inner.withObfuscated(obfuscated), color, offset);
    }

    @Override
    public Style withClickEvent(@Nullable ClickEvent clickEvent) {
        return new RainbowStyle(inner.withClickEvent(clickEvent), color, offset);
    }

    @Override
    public Style withHoverEvent(@Nullable HoverEvent hoverEvent) {
        return new RainbowStyle(inner.withHoverEvent(hoverEvent), color, offset);
    }

    @Override
    public Style withInsertion(@Nullable String insertion) {
        return new RainbowStyle(inner.withInsertion(insertion), color, offset);
    }

    @Override
    public Style withFont(@Nullable Identifier font) {
        return new RainbowStyle(inner.withFont(font), color, offset);
    }

    @Override
    public Style withFormatting(Formatting formatting) {
        return new RainbowStyle(inner.withFormatting(formatting), color, offset);
    }

    @Override
    public Style withExclusiveFormatting(Formatting formatting) {
        return new RainbowStyle(inner.withExclusiveFormatting(formatting), color, offset);
    }

    @Override
    public Style withFormatting(Formatting... formattings) {
        return new RainbowStyle(inner.withFormatting(formattings), color, offset);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound compound = new NbtCompound();
        compound.put("color", color.toTag());
        return compound;
    }

    @Override
    public RainbowStyle fromTag(NbtCompound tag) {
        this.color.fromTag(tag.getCompound("color"));
        return this;
    }
}
