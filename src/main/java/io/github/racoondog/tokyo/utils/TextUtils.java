package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.mixin.IStyle;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;

import java.util.*;

@Environment(EnvType.CLIENT)
public final class TextUtils {
    /* Prefix */

    public static Text colorFade(String text, Color from, Color to) {
        return colorFade(text, from.getPacked(), to.getPacked());
    }

    public static Text colorFade(String text, int from, int to) {
        return colorFade(text, Style.EMPTY, from, to);
    }

    public static Text colorFade(String text, Style rootStyle, Color from, Color to) {
        return colorFade(text, rootStyle, from.getPacked(), to.getPacked());
    }

    public static Text colorFade(String text, Style rootStyle, int from, int to) {
        MutableText characterRoot = Text.empty().setStyle(rootStyle);

        Text[] fadeText = generateFadeText(text, rootStyle, from, to);

        for (var value : fadeText) {
            characterRoot.append(value);
        }

        return characterRoot;
    }

    public static Text[] generateFadeText(String text, Style rootStyle, Color from, Color to) {
        return generateFadeText(text, rootStyle, from.getPacked(), to.getPacked());
    }

    public static Text[] generateFadeText(String text, Style rootStyle, int from, int to) {
        String[] characters = text.split("");
        Text[] output = new Text[characters.length];

        for (int i = 0; i < characters.length; i++) {
            output[i] = Text.literal(characters[i])
                .setStyle(rootStyle.withColor(TextColor.fromRgb(new Color(
                    MathHelper.lerp(((float) i) / characters.length, Color.toRGBAR(from), Color.toRGBAR(to)),
                    MathHelper.lerp(((float) i) / characters.length, Color.toRGBAG(from), Color.toRGBAG(to)),
                    MathHelper.lerp(((float) i) / characters.length, Color.toRGBAB(from), Color.toRGBAB(to))
                ).getPacked())));
        }

        return output;
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

    /* Chat Emojis */


    public static List<Pair<String, Style>> dissect(OrderedText orderedText) {
        List<Pair<String, Style>> list = new ArrayList<>();

        orderedText.accept((index, style, codePoint) -> {
            if (list.isEmpty() || !list.get(list.size() - 1).right().equals(style)) {
                list.add(new ObjectObjectMutablePair<>(Character.toString(codePoint), style));
            } else {
                Pair<String, Style> last = list.get(list.size() - 1);
                last.left(last.left() + Character.toString(codePoint));
            }

            return true;
        });

        return list;
    }

    public static OrderedText toOrderedText(String text, Style style) {
        return Text.literal(text).setStyle(style).asOrderedText();
    }
}
