package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.mixin.prefix.IFontManager;
import io.github.racoondog.tokyo.mixin.prefix.IMinecraftClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.IScreenFactory;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.Set;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class MinecraftFont implements IScreenFactory, ICopyable<MinecraftFont>, ISerializable<MinecraftFont> {
    private Identifier font;

    public MinecraftFont() {
        font = Style.DEFAULT_FONT_ID;
    }

    public MinecraftFont(Identifier identifier) {
        font = identifier;
    }

    public Identifier get() {
        return font;
    }

    @Override
    public WidgetScreen createScreen(GuiTheme theme) {
        return new MinecraftFontScreen(theme, this);
    }

    @Override
    public MinecraftFont set(MinecraftFont value) {
        font = value.font;
        return this;
    }

    @Override
    public MinecraftFont copy() {
        return new MinecraftFont(font);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound compound = new NbtCompound();

        compound.putString("font", font.toString());

        return compound;
    }

    @Override
    public MinecraftFont fromTag(NbtCompound tag) {
        try {
            font = Identifier.tryParse(tag.getString("font"));
        } catch (InvalidIdentifierException ignored) {}

        return this;
    }

    public static class MinecraftFontScreen extends WindowScreen {
        private final MinecraftFont font;

        public MinecraftFontScreen(GuiTheme theme, MinecraftFont font) {
            super(theme, "Select Font");

            this.font = font;
        }

        @Override
        public void initWidgets() {
            WHorizontalList fontList = add(theme.horizontalList()).expandX().widget();
            WDropdown<String> fontDropdown = fontList.add(theme.dropdown(getLoadedFonts(), font.font.toString())).expandWidgetX().widget();
            fontDropdown.action = () -> {
                try {
                    font.font = Identifier.tryParse(fontDropdown.get());
                } catch (InvalidIdentifierException ignored) {}
            };
            WButton resetButton = fontList.add(theme.button(GuiRenderer.RESET)).right().widget();
            resetButton.action = () -> {
                font.font = Style.DEFAULT_FONT_ID;
                fontDropdown.set(Style.DEFAULT_FONT_ID.toString());
            };

            onClosed(() -> {
                try {
                    font.font = Identifier.tryParse(fontDropdown.get());
                } catch (InvalidIdentifierException ignored) {}
            });
        }

        @SuppressWarnings("resource")
        private static String[] getLoadedFonts() {
            Set<Identifier> fontIdentifiers = ((IFontManager) ((IMinecraftClient) mc).tokyo$getFontManager()).tokyo$getFontStorages().keySet();
            String[] fontNames = new String[fontIdentifiers.size()];

            int index = 0;
            for (var identifier : fontIdentifiers) {
                fontNames[index++] = identifier.toString();
            }

            return fontNames;
        }
    }
}
