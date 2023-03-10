package io.github.racoondog.tokyo.utils.misc;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.chars.CharPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public final class StringUtils {
    private static final Random RANDOM = ThreadLocalRandom.current();

    public static Pattern[] compile(String... regexes) {
        Pattern[] patterns = new Pattern[regexes.length];

        for (int i = 0; i < regexes.length; i++) {
            patterns[i] = Pattern.compile(regexes[i]);
        }

        return patterns;
    }

    public static String[] recursiveSplit(String root, Pattern... regexes) {
        List<String> tokens = Lists.newArrayList(root);

        for (var regex : regexes) {
            for (int i = 0; i < tokens.size(); i++) {
                String[] split = regex.split(tokens.get(i));

                if (split.length > 1) {
                    tokens.remove(i);
                    tokens.addAll(i, Arrays.asList(split));
                    i += split.length - 1;
                }
            }
        }

        return tokens.toArray(new String[0]);
    }

    public static String[] recursiveSplit(String root, String... regexes) {
        return recursiveSplit(root, compile(regexes));
    }

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

    public static boolean isMostlyUppercase(CharSequence seq) {
        return isMostlyUppercase(seq, 0, seq.length());
    }

    public static boolean isMostlyUppercase(CharSequence seq, int index, int len) {
        int upper = 0;
        int endIndex = Math.min(index + len, seq.length());
        for (int i = index; i < endIndex; i++) {
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

    public static boolean listContainsIgnoreCase(List<? extends CharSequence> strings, String token) {
        for (var str : strings) {
            if (str.toString().equalsIgnoreCase(token)) return true;
        }
        return false;
    }
}
