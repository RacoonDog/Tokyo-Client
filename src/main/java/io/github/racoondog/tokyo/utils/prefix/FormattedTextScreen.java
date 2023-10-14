package io.github.racoondog.tokyo.utils.prefix;

import io.github.racoondog.tokyo.utils.settings.MinecraftFont;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FormattedTextScreen extends WindowScreen {
    private final FormattedText formattedText;
    private final Settings settings = new Settings();

    public FormattedTextScreen(GuiTheme theme, FormattedText formattedText) {
        super(theme, "Format Text");

        this.formattedText = formattedText;
    }

    @Override
    public void tick() {
        settings.tick(window, theme);
    }

    @Override
    public void initWidgets() {
        SettingGroup sgGeneral = settings.getDefaultGroup();
        SettingGroup sgStyle = settings.createGroup("Style");
        SettingGroup sgFade = settings.createGroup("Fade");
        SettingGroup sgRainbow = settings.createGroup("Rainbow");

        //General
        sgGeneral.add(new StringSetting.Builder()
            .name("text")
            .defaultValue(formattedText.defaultText)
            .onModuleActivated(stringSetting -> stringSetting.set(formattedText.text))
            .onChanged(text -> {
                formattedText.text = text;
                formattedText.onChanged();
            })
            .build()
        );

        sgGeneral.add(new EnumSetting.Builder<FormattedText.Mode>()
            .name("mode")
            .defaultValue(formattedText.defaultMode)
            .onModuleActivated(enumSetting -> enumSetting.set(formattedText.mode))
            .onChanged(mode -> {
                formattedText.mode = mode;
                formattedText.onChanged();
            })
            .build()
        );

        //Style
        sgStyle.add(new ColorSetting.Builder()
            .name("text-color")
            .defaultValue(new SettingColor(formattedText.defaultStyle.getColor().getRgb()))
            .onModuleActivated(colorSetting -> colorSetting.set(new SettingColor(formattedText.style.getColor().getRgb())))
            .onChanged(color -> {
                formattedText.style = formattedText.style.withColor(color.getPacked());
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Regular)
            .build()
        );

        sgStyle.add(new BoolSetting.Builder()
            .name("bold")
            .defaultValue(formattedText.defaultStyle.isBold())
            .onModuleActivated(boolSetting -> boolSetting.set(formattedText.style.isBold()))
            .onChanged(bool -> {
                formattedText.style = formattedText.style.withBold(bool);
                formattedText.onChanged();
            })
            .build()
        );

        sgStyle.add(new BoolSetting.Builder()
            .name("italic")
            .defaultValue(formattedText.defaultStyle.isItalic())
            .onModuleActivated(boolSetting -> boolSetting.set(formattedText.style.isItalic()))
            .onChanged(bool -> {
                formattedText.style = formattedText.style.withItalic(bool);
                formattedText.onChanged();
            })
            .build()
        );

        sgStyle.add(new BoolSetting.Builder()
            .name("underlined")
            .defaultValue(formattedText.defaultStyle.isUnderlined())
            .onModuleActivated(boolSetting -> boolSetting.set(formattedText.style.isUnderlined()))
            .onChanged(bool -> {
                formattedText.style = formattedText.style.withUnderline(bool);
                formattedText.onChanged();
            })
            .build()
        );

        sgStyle.add(new BoolSetting.Builder()
            .name("strikethrough")
            .defaultValue(formattedText.defaultStyle.isStrikethrough())
            .onModuleActivated(boolSetting -> boolSetting.set(formattedText.style.isStrikethrough()))
            .onChanged(bool -> {
                formattedText.style = formattedText.style.withStrikethrough(bool);
                formattedText.onChanged();
            })
            .build()
        );

        sgStyle.add(new BoolSetting.Builder()
            .name("obfuscated")
            .defaultValue(formattedText.defaultStyle.isObfuscated())
            .onModuleActivated(boolSetting -> boolSetting.set(formattedText.style.isObfuscated()))
            .onChanged(bool -> {
                formattedText.style = formattedText.style.withObfuscated(bool);
                formattedText.onChanged();
            })
            .build()
        );

        sgStyle.add(new GenericSetting.Builder<MinecraftFont>()
            .name("font")
            .defaultValue(new MinecraftFont())
            .onChanged(font -> {
                formattedText.style = formattedText.style.withFont(font.get());
                formattedText.onChanged();
            })
            .build()
        );

        //Fade

        sgFade.add(new ColorSetting.Builder()
            .name("from")
            .defaultValue(formattedText.defaultColorFrom.toSetting())
            .onModuleActivated(colorSetting -> colorSetting.set(formattedText.from.toSetting()))
            .onChanged(color -> {
                formattedText.from.set(color);
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Fade)
            .build()
        );

        sgFade.add(new ColorSetting.Builder()
            .name("to")
            .defaultValue(formattedText.defaultColorTo.toSetting())
            .onModuleActivated(colorSetting -> colorSetting.set(formattedText.to.toSetting()))
            .onChanged(color -> {
                formattedText.to.set(color);
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Fade)
            .build()
        );

        sgFade.add(new BoolSetting.Builder()
            .name("lerp-hsv")
            .description("Fades based on the HSV color space instead of RGB.")
            .defaultValue(formattedText.hsv)
            .onModuleActivated(colorSetting -> colorSetting.set(formattedText.hsv))
            .onChanged(bool -> {
                formattedText.hsv = bool;
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Fade)
            .build()
        );

        //Rainbow

        sgRainbow.add(new DoubleSetting.Builder()
            .name("speed")
            .defaultValue(0.05d)
            .range(0.0d, 1.0d)
            .sliderRange(0.0d, 1.0d)
            .decimalPlaces(2)
            .onModuleActivated(doubleSetting -> doubleSetting.set(formattedText.rainbowColor.speed))
            .onChanged(d -> {
                formattedText.rainbowColor.speed = d;
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Rainbow)
            .build()
        );

        sgRainbow.add(new DoubleSetting.Builder()
            .name("brightness")
            .defaultValue(1.0d)
            .range(0.0d, 1.0d)
            .sliderRange(0.0d, 1.0d)
            .decimalPlaces(2)
            .onModuleActivated(doubleSetting -> doubleSetting.set(formattedText.rainbowColor.brightness))
            .onChanged(d -> {
                formattedText.rainbowColor.brightness = d;
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Rainbow)
            .build()
        );

        sgRainbow.add(new DoubleSetting.Builder()
            .name("saturation")
            .defaultValue(0.75d)
            .range(0.0d, 1.0d)
            .sliderRange(0.0d, 1.0d)
            .decimalPlaces(2)
            .onModuleActivated(doubleSetting -> doubleSetting.set(formattedText.rainbowColor.saturation))
            .onChanged(d -> {
                formattedText.rainbowColor.saturation = d;
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Rainbow)
            .build()
        );

        sgRainbow.add(new DoubleSetting.Builder()
            .name("horizontal-offset")
            .defaultValue(0.05d)
            .range(-1.0d, 1.0d)
            .sliderRange(-1.0d, 1.0d)
            .decimalPlaces(2)
            .onModuleActivated(doubleSetting -> doubleSetting.set(formattedText.rainbowColor.horizontalOffset))
            .onChanged(d -> {
                formattedText.rainbowColor.horizontalOffset = d;
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Rainbow)
            .build()
        );

        sgRainbow.add(new DoubleSetting.Builder()
            .name("vertical-offset")
            .defaultValue(-0.05d)
            .range(-1.0d, 1.0d)
            .sliderRange(-1.0d, 1.0d)
            .decimalPlaces(2)
            .onModuleActivated(doubleSetting -> doubleSetting.set(formattedText.rainbowColor.verticalOffset))
            .onChanged(d -> {
                formattedText.rainbowColor.verticalOffset = d;
                formattedText.onChanged();
            })
            .visible(() -> formattedText.mode == FormattedText.Mode.Rainbow)
            .build()
        );

        settings.onActivated();
        add(theme.settings(settings)).expandX();
    }
}
