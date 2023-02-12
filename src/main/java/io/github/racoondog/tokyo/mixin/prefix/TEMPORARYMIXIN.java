package io.github.racoondog.tokyo.mixin.prefix;

import io.github.racoondog.meteorsharedaddonutils.utils.ColorUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

//todo implement well
@Environment(EnvType.CLIENT)
@Mixin(value = ColorUtils.class, remap = false)
public abstract class TEMPORARYMIXIN {
    @Shadow public static SettingColor fromFormatting(Formatting formatting, SettingColor fallback) {
        throw new AssertionError();
    }
    @Shadow public static SettingColor fromTextColor(@Nullable TextColor textColor, SettingColor fallback) {
        throw new AssertionError();
    }
    @Shadow public static SettingColor fromStyle(Style style, SettingColor fallback) {
        throw new AssertionError();
    }

    @Unique private static final SettingColor WHITE = new SettingColor(255, 255, 255);

    @Overwrite
    public static SettingColor fromFormatting(Formatting formatting) {
        return fromFormatting(formatting, WHITE);
    }

    @Overwrite
    public static SettingColor fromTextColor(@Nullable TextColor textColor) {
        return fromTextColor(textColor, WHITE);
    }

    @Overwrite
    public static SettingColor fromStyle(Style style) {
        return fromStyle(style, WHITE);
    }
}
