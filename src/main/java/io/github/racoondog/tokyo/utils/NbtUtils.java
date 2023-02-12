package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.mixin.prefix.IStyle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.*;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class NbtUtils {
    public static NbtCompound serializeStyle(Style style) {
        NbtCompound compound = new NbtCompound();
        IStyle accessor = (IStyle) style;

        putNullable(compound, Optional.ofNullable(style.getColor()).map(TextColor::getRgb), "color");
        putNullable(compound, Optional.ofNullable(accessor.tokyo$getBold()), "bold");
        putNullable(compound, Optional.ofNullable(accessor.tokyo$getItalic()), "italic");
        putNullable(compound, Optional.ofNullable(accessor.tokyo$getUnderlined()), "underlined");
        putNullable(compound, Optional.ofNullable(accessor.tokyo$getStrikethrough()), "strikethrough");
        putNullable(compound, Optional.ofNullable(accessor.tokyo$getObfuscated()), "obfuscated");
        putNullable(compound, Optional.ofNullable(accessor.tokyo$getFont()).map(Identifier::toString), "font");

        return compound;
    }

    public static Style deserializeStyle(NbtCompound compound) {
        Style style = Style.EMPTY;

        if (compound.contains("color")) style = style.withColor(TextColor.fromRgb(compound.getInt("color")));
        if (compound.contains("bold")) style = style.withBold(compound.getBoolean("bold"));
        if (compound.contains("italic")) style = style.withItalic(compound.getBoolean("italic"));
        if (compound.contains("underlined")) style = style.withUnderline(compound.getBoolean("underlined"));
        if (compound.contains("strikethrough")) style = style.withStrikethrough(compound.getBoolean("strikethrough"));
        if (compound.contains("obfuscated")) style = style.withObfuscated(compound.getBoolean("obfuscated"));
        if (compound.contains("font")) {
            try {
                style = style.withFont(Identifier.tryParse(compound.getString("font")));
            } catch (InvalidIdentifierException ignored) {}
        }

        return style;
    }

    public static NbtCompound serializeEnum(Enum<?> enumValue) {
        NbtCompound compound = new NbtCompound();
        compound.putInt("ordinal", enumValue.ordinal());
        return compound;
    }

    public static <T extends Enum<?>> T deserializeEnum(NbtCompound compound, Class<T> clazz) {
        int ordinal = compound.getInt("ordinal");
        return clazz.getEnumConstants()[ordinal];
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static void putNullable(NbtCompound compound, Optional<?> o, String key) {
        if (o.isEmpty()) return;
        NbtElement e = element(o.get());
        if (e != null) compound.put(key, e);
    }

    private static NbtElement element(Object o) {
        if (o instanceof Boolean b) return NbtByte.of(b);
        if (o instanceof Byte b) return NbtByte.of(b);
        if (o instanceof Double d) return NbtDouble.of(d);
        if (o instanceof CharSequence c) return NbtString.of(c.toString());
        if (o instanceof Float f) return NbtFloat.of(f);
        if (o instanceof Integer i) return NbtInt.of(i);
        if (o instanceof Long l) return NbtLong.of(l);

        return null;
    }
}
