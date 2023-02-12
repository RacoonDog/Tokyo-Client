package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.mixin.prefix.IStyle;
import it.unimi.dsi.fastutil.chars.CharPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Range;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class StringUtils {
    private static final Random RANDOM = ThreadLocalRandom.current();

    public static int countChars(CharSequence text, char... characters) {
        if (characters.length <= 0) return 0;

        //Build predicate
        CharPredicate predicate = c -> c == characters[0];
        for (int i = 1; i < characters.length; i++) {
            char ch = characters[i];
            predicate = predicate.or(c -> c == ch);
        }

        int charCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (predicate.test(c)) charCount++;
        }

        return charCount;
    }

    public static String uniformChanceAppend(String text, CharSequence[] append, double totalChance) {
        if (totalChance <= 0.0d) return text;
        totalChance = Math.max(totalChance, 1.0d);

        double localChance = totalChance / append.length;

        for (var str : append) {
            if (RANDOM.nextFloat() < localChance) {
                return text.concat(str.toString());
            }
        }
        return text;
    }

    public static void uniformChanceAppend(StringBuilder text, CharSequence[] append, double totalChance) {
        double localChance = totalChance / append.length;

        for (var str : append) {
            if (RANDOM.nextFloat() < localChance) {
                text.append(str);
                return;
            }
        }
    }

    public static boolean isLastCharAlphabetic(CharSequence seq) {
        return Character.isAlphabetic(seq.charAt(seq.length() - 1));
    }

    public static String randomChanceKeepCaseReplace(String text, CharSequence target, CharSequence replacement, double chance) {
        if (chance <= 0.0d) return text;
        if (chance >= 1.0d) return text.replace(target, replacement);

        StringBuilder sb = new StringBuilder(text);
        randomChanceKeepCaseReplace(sb, target, replacement, chance);
        return sb.toString();
    }

    public static void randomChanceKeepCaseReplace(StringBuilder text, CharSequence target, CharSequence replacement, double chance) {
        String targetString = target.toString();
        String replacementString = replacement.toString();

        int push = 0;
        int lenDiff = replacementString.length() - targetString.length();
        int index = org.apache.commons.lang3.StringUtils.indexOfIgnoreCase(text, targetString);
        while (index != -1) {
            if (RANDOM.nextDouble() > chance) {
                boolean upper = isMostlyUppercase(text, index, targetString.length());

                int endIdx = index + replacementString.length();
                text.replace(index, endIdx + push, upper ? replacementString.toUpperCase(Locale.ROOT) : replacementString.toLowerCase(Locale.ROOT));
                push += lenDiff;

                index = org.apache.commons.lang3.StringUtils.indexOfIgnoreCase(text, targetString, endIdx);
            } else index = org.apache.commons.lang3.StringUtils.indexOfIgnoreCase(text, targetString, index + targetString.length());
        }
    }

    public static String randomChanceReplace(String text, CharSequence target, CharSequence replacement, double chance) {
        if (chance <= 0.0d) return text;
        if (chance >= 1.0d) return text.replace(target, replacement);

        StringBuilder sb = new StringBuilder(text);
        randomChanceReplace(sb, target, replacement, chance);
        return sb.toString();
    }

    public static void randomChanceReplace(StringBuilder text, CharSequence target, CharSequence replacement, double chance) {
        String targetString = target.toString();
        String replacementString = replacement.toString();

        int push = 0;
        int lenDiff = replacementString.length() - targetString.length();
        int index = text.indexOf(targetString);
        while (index != -1) {
            if (RANDOM.nextDouble() > chance) {
                int endIdx = index + replacementString.length();
                text.replace(index, endIdx + push, replacementString);
                push += lenDiff;

                index = text.indexOf(targetString, endIdx);
            } else index = text.indexOf(targetString, index + targetString.length());
        }
    }

    public static void keepCaseReplace(StringBuilder text, CharSequence target, CharSequence replacement) {
        String targetString = target.toString();
        String replacementString = replacement.toString();

        int push = 0;
        int lenDiff = replacementString.length() - targetString.length();
        int index = org.apache.commons.lang3.StringUtils.indexOfIgnoreCase(text, targetString);
        while (index != -1) {
            boolean upper = isMostlyUppercase(text, index, targetString.length());

            int endIdx = index + replacementString.length();
            text.replace(index, endIdx + push, upper ? replacementString.toUpperCase(Locale.ROOT) : replacementString.toLowerCase(Locale.ROOT));
            push += lenDiff;

            index = org.apache.commons.lang3.StringUtils.indexOfIgnoreCase(text, targetString, endIdx);
        }
    }

    public static boolean isMostlyUppercase(CharSequence seq, int index, int len) {
        int upper = 0;
        for (int i = index; i < index + len; i++) {
            if (Character.isUpperCase(seq.charAt(i))) upper++;
        }
        return upper * 2 >= len + 1;
    }

    /**
     * char implementation of {@link org.apache.commons.lang3.StringUtils#indexOfIgnoreCase(CharSequence, CharSequence, int)}
     * @author Crosby
     */
    public static int indexOfIgnoreCase(CharSequence str, int ch, int startIndex) {
        if (str == null) return -1;

        startIndex = Math.max(startIndex, 0);

        if (str instanceof String strString) {
            int lowerIdx = strString.indexOf(Character.toLowerCase(ch), startIndex);
            int upperIdx = strString.indexOf(Character.toUpperCase(ch), startIndex);

            if (lowerIdx != -1) {
                if (upperIdx != -1) return Math.min(lowerIdx, upperIdx);
                else return lowerIdx;
            } else return upperIdx;
        } else {
            char upper = (char) Character.toUpperCase(ch);
            char lower = (char) Character.toLowerCase(ch);

            for (int i = startIndex; i < str.length(); i++) {
                char c = str.charAt(i);

                if (upper == c || lower == c) return i;
            }
        }

        return -1;
    }

    public static int indexOfIgnoreCase(CharSequence str, int ch) {
        return indexOfIgnoreCase(str, ch, 0);
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