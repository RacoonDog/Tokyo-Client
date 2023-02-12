package io.github.racoondog.tokyo.utils.prefix;

import io.github.racoondog.tokyo.utils.NbtUtils;
import io.github.racoondog.tokyo.utils.StringUtils;
import io.github.racoondog.tokyo.utils.TextUtils;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.utils.IScreenFactory;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class FormattedText implements IScreenFactory, ICopyable<FormattedText>, ISerializable<FormattedText> {
    //Defaults
    public final String defaultText;
    public final Mode defaultMode;
    public final Style defaultStyle;
    public final Color defaultColorFrom;
    public final Color defaultColorTo;

    public String text;
    public Mode mode;
    public Style style;
    public Color from;
    public Color to;

    private @Nullable Consumer<FormattedText> onChanged;
    public RainbowColor rainbowColor = new RainbowColor();

    public FormattedText(String defaultText, Mode defaultMode, Style defaultStyle, Color defaultColorFrom, Color defaultColorTo) {
        this.defaultText = text = defaultText;
        this.defaultMode = mode = defaultMode;
        this.defaultStyle = defaultStyle;
        style = StringUtils.cloneStyle(defaultStyle);
        this.defaultColorFrom = defaultColorFrom;
        from = defaultColorFrom.copy();
        this.defaultColorTo = defaultColorTo;
        to = defaultColorTo.copy();
    }

    public FormattedText(String defaultText, Color defaultColor, Color defaultFadeFromColor, Color defaultFadeToColor) {
        this(defaultText, Mode.Regular, Style.EMPTY.withColor(defaultColor.getPacked()), defaultFadeFromColor, defaultFadeToColor);
    }

    public FormattedText(String defaultText, Color defaultColor) {
        this(defaultText, defaultColor, defaultColor, defaultColor);
    }

    public FormattedText(String defaultText) {
        this(defaultText, Color.WHITE);
    }

    public Text get() {
        return switch (mode) {
            case Regular -> Text.literal(text).setStyle(style);
            case Fade -> TextUtils.colorFade(text, style, from, to);
            case Rainbow -> RainbowStyle.fadeOffset(text, style, rainbowColor);
        };
    }

    public void onChanged() {
        if (onChanged != null) onChanged.accept(this);
    }

    public FormattedText onChanged(Consumer<FormattedText> consumer) {
        onChanged = consumer;
        return this;
    }

    @Override
    public WidgetScreen createScreen(GuiTheme theme) {
        return new FormattedTextScreen(theme, this);
    }

    @Override
    public FormattedText set(FormattedText value) {
        text = value.text;
        mode = value.mode;
        style = StringUtils.cloneStyle(value.style);
        from = value.from.copy();
        to = value.to.copy();
        rainbowColor = value.rainbowColor;
        onChanged = value.onChanged;
        return this;
    }

    @Override
    public FormattedText copy() {
        FormattedText formattedText = new FormattedText(defaultText);
        formattedText.text = text;
        formattedText.mode = mode;
        formattedText.style = StringUtils.cloneStyle(style);
        formattedText.from = from.copy();
        formattedText.to = to.copy();
        formattedText.rainbowColor = rainbowColor;
        formattedText.onChanged = onChanged;
        return formattedText;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound compound = new NbtCompound();

        compound.putString("text", text);
        compound.put("mode", NbtUtils.serializeEnum(mode));
        compound.put("style", NbtUtils.serializeStyle(style));

        compound.put("from", from.toTag());
        compound.put("to", to.toTag());

        compound.put("rainbowColor", rainbowColor.toTag());

        return compound;
    }

    @Override
    public FormattedText fromTag(NbtCompound tag) {
        text = tag.getString("text");
        mode = NbtUtils.deserializeEnum(tag.getCompound("mode"), Mode.class);
        style = NbtUtils.deserializeStyle(tag.getCompound("style"));

        from.fromTag(tag.getCompound("from"));
        to.fromTag(tag.getCompound("to"));

        rainbowColor.fromTag(tag.getCompound("rainbowColor"));

        return this;
    }

    @Override
    public String toString() {
        return "%s[mode=%s,style=%s,from=%s,to=%s,rainbow=%s]"
            .formatted(text, mode.name(), style, from, to, rainbowColor);
    }

    public enum Mode {
        Regular,
        Fade,
        Rainbow
    }
}
