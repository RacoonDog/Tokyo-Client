package io.github.racoondog.tokyo.systems.hud;

import io.github.racoondog.tokyo.mixininterface.IRenderer2D;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class ImageHud extends HudElement {
    public static final HudElementInfo<ImageHud> INFO = new HudElementInfo<>(Hud.GROUP, "image", "Displays arbitrary images.", ImageHud::new);

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgCustomization = this.settings.createGroup("Customization");

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.Preset)
        .onChanged(s -> update())
        .build()
    );

    private final Setting<Preset> preset = sgGeneral.add(new EnumSetting.Builder<Preset>()
        .name("preset")
        .defaultValue(Preset.Meteor)
        .visible(() -> mode.get() == Mode.Preset)
        .onChanged(s -> update())
        .build()
    );

    private final Setting<String> resource = sgGeneral.add(new StringSetting.Builder()
        .name("resource")
        .defaultValue("minecraft:textures/misc/unknown_server.png")
        .visible(() -> mode.get() == Mode.Resource)
        .onChanged(s -> update())
        .build()
    );

    private final Setting<String> url = sgGeneral.add(new StringSetting.Builder()
        .name("url")
        .defaultValue("https://raw.githubusercontent.com/RacoonDog/RacoonDog/main/csnail.PNG")
        .visible(() -> mode.get() == Mode.Online)
        .onChanged(s -> update())
        .build()
    );

    // Customization

    private final Setting<Double> scale = sgCustomization.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale of the image.")
        .defaultValue(2)
        .min(0.1)
        .sliderRange(0.1, 10)
        .onChanged(o -> updateSize())
        .build()
    );
    private final Setting<Boolean> hFlip = sgCustomization.add(new BoolSetting.Builder()
        .name("horizontal-flip")
        .description("Flips the image horizontally.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> vFlip = sgCustomization.add(new BoolSetting.Builder()
        .name("vertical-flip")
        .description("Flips the image vertically.")
        .defaultValue(false)
        .build()
    );

    private final Setting<SettingColor> colorFilter = sgCustomization.add(new ColorSetting.Builder()
        .name("color-filter")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    private NativeImageBackedTexture ONLINE;
    private Identifier TEXTURE;
    private int width;
    private int height;

    private ImageHud() {
        super(INFO);
    }

    private void update() {
        switch (mode.get()) {
            case Preset -> TEXTURE = preset.get().identifier;
            case Resource -> TEXTURE = Identifier.tryParse(resource.get());
            case Online -> {
                ONLINE = null;
                MeteorExecutor.execute(() -> {
                    String imgUrl = url.get();
                    if (imgUrl.isEmpty()) return;
                    try {
                        NativeImage img = NativeImage.read(Http.get(imgUrl).sendInputStream());
                        width = img.getWidth();
                        height = img.getHeight();
                        ONLINE = new NativeImageBackedTexture(img);
                        updateSize();
                    } catch (IOException ignored) {}
                });
            }
        }
    }

    public void updateSize() {
        double width = mode.get() == Mode.Online ? this.width : 64;
        double height = mode.get() == Mode.Online ? this.height : 64;

        setSize(width * scale.get(), height * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mode.get() == Mode.Online && ONLINE != null) ONLINE.upload();
        else GL.bindTexture(this.TEXTURE);

        Renderer2D.TEXTURE.begin();

        if (hFlip.get() && vFlip.get()) IRenderer2D.TEXTURE.texQuadHVFlip(x, y, getWidth(), getHeight(), colorFilter.get());
        else if (hFlip.get()) IRenderer2D.TEXTURE.texQuadHFlip(x, y, getWidth(), getHeight(), colorFilter.get());
        else if (vFlip.get()) IRenderer2D.TEXTURE.texQuadVFlip(x, y, getWidth(), getHeight(), colorFilter.get());
        else Renderer2D.TEXTURE.texQuad(x, y, getWidth(), getHeight(), colorFilter.get());

        Renderer2D.TEXTURE.render(null);
    }

    public enum Mode {
        Preset,
        Resource,
        Online
    }

    public enum Preset {
        Meteor("meteor-client:textures/meteor.png");

        final Identifier identifier;

        Preset(String location) {
            this.identifier = Identifier.tryParse(location);
        }
    }
}
