package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.mixin.IStyle;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public final class TextUtils {
    /* Prefix */

    public static Text colorFade(String text, Color from, Color to, boolean hsv) {
        return colorFade(text, from.getPacked(), to.getPacked(), hsv);
    }

    public static Text colorFade(String text, int from, int to, boolean hsv) {
        return colorFade(text, Style.EMPTY, from, to, hsv);
    }

    public static Text colorFade(String text, Style rootStyle, Color from, Color to, boolean hsv) {
        return colorFade(text, rootStyle, from.getPacked(), to.getPacked(), hsv);
    }

    public static Text colorFade(String text, Style rootStyle, int from, int to, boolean hsv) {
        MutableText characterRoot = Text.empty().setStyle(rootStyle);

        Text[] fadeText = generateFadeText(text, rootStyle, from, to, hsv);

        for (var value : fadeText) {
            characterRoot.append(value);
        }

        return characterRoot;
    }

    public static Text[] generateFadeText(String text, Style rootStyle, Color from, Color to, boolean hsb) {
        return generateFadeText(text, rootStyle, from.getPacked(), to.getPacked(), hsb);
    }

    public static Text[] generateFadeText(String text, Style rootStyle, int from, int to, boolean hsv) {
        String[] characters = text.split("");
        Text[] output = new Text[characters.length];

        for (int i = 0; i < characters.length; i++) {
            output[i] = Text.literal(characters[i])
                .setStyle(rootStyle.withColor(TextColor.fromRgb(
                    hsv ? lerpHsv(i / (characters.length - 1f), from, to) : lerpRgb(i / (characters.length - 1f), from, to)
                )));
        }

        return output;
    }

    public static int lerpRgb(float delta, int from, int to) {
        int r = MathHelper.lerp(delta, Color.toRGBAR(from), Color.toRGBAR(to));
        int g = MathHelper.lerp(delta, Color.toRGBAG(from), Color.toRGBAG(to));
        int b = MathHelper.lerp(delta, Color.toRGBAB(from), Color.toRGBAB(to));
        int a = MathHelper.lerp(delta, Color.toRGBAA(from), Color.toRGBAA(to));

        return Color.fromRGBA(r, g, b, a);
    }

    public static int lerpHsv(float delta, int from, int to) {
        float[] fromHsb = new float[3];
        java.awt.Color.RGBtoHSB(Color.toRGBAR(from), Color.toRGBAG(from), Color.toRGBAB(from), fromHsb);

        float[] toHsb = new float[3];
        java.awt.Color.RGBtoHSB(Color.toRGBAR(to), Color.toRGBAG(to), Color.toRGBAB(to), toHsb);

        float h = MathHelper.lerp(delta, fromHsb[0], toHsb[0]);
        float s = MathHelper.lerp(delta, fromHsb[1], toHsb[1]);
        float b = MathHelper.lerp(delta, fromHsb[2], toHsb[2]);
        int a = MathHelper.lerp(delta, Color.toRGBAA(from), Color.toRGBAA(to));

        return new Color(java.awt.Color.HSBtoRGB(h, s, b)).a(a).getPacked();
    }

    public static Style cloneStyle(Style root) {
        IStyle accessor = (IStyle) root;

        return Style.EMPTY.withColor(accessor.tokyo$getColor())
            .withBold(accessor.tokyo$getBold())
            .withItalic(accessor.tokyo$getItalic())
            .withUnderline(accessor.tokyo$getUnderlined())
            .withStrikethrough(accessor.tokyo$getStrikethrough())
            .withObfuscated(accessor.tokyo$getObfuscated())
            .withClickEvent(accessor.tokyo$getClickEvent())
            .withHoverEvent(accessor.tokyo$getHoverEvent())
            .withInsertion(accessor.tokyo$getInsertion())
            .withFont(accessor.tokyo$getFont());
    }
}
