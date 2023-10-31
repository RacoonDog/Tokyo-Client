package io.github.racoondog.tokyo.systems.hud;

import com.mojang.blaze3d.platform.TextureUtil;
import io.github.racoondog.tokyo.mixininterface.IRenderer2D;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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

    private boolean firstRender = true;
    private CompletableFuture<Supplier<AbstractTexture>> textureFetcher;
    private AbstractTexture TEXTURE;
    private int width;
    private int height;

    private ImageHud() {
        super(INFO);
    }

    private void update() {
        resetTexture();

        Supplier<Supplier<AbstractTexture>> supplier = () -> switch (mode.get()) {
            case Online -> {
                String imgUrl = url.get();
                if (!imgUrl.isEmpty()) {
                    try {
                        NativeImage img = NativeImage.read(Http.get(imgUrl).sendInputStream());
                        width = img.getWidth();
                        height = img.getHeight();
                        updateSize();
                        yield () -> new NativeImageBackedTexture(img);
                    } catch (IOException ignored) {}
                }
                yield () -> null;
            }
            case Preset -> {
                preset.get().updateSize(this);
                yield () -> mc.getTextureManager().getTexture(preset.get().identifier);
            }
            case Resource -> {
                Identifier textureId = Identifier.tryParse(resource.get());

                if (textureId != null) {
                    Optional<Resource> optionalResource = mc.getResourceManager().getResource(textureId);
                    if (optionalResource.isPresent()) {
                        metaFromResource(optionalResource.get());
                        updateSize();
                        yield () -> mc.getTextureManager().getTexture(textureId);
                    }
                }
                yield () -> null;
            }
        };

        textureFetcher = CompletableFuture.supplyAsync(supplier);
    }

    private void resetTexture() {
        TEXTURE = null;
        width = 64;
        height = 64;
        updateSize();
    }

    public void updateSize() {
        setSize(width * scale.get(), height * scale.get());
    }

    protected void metaFromResource(Resource resource) {
        try (var inputStream = resource.getInputStream()) {
            ByteBuffer buffer = TextureUtil.readResource(inputStream).rewind();
            int[] width = new int[1];
            int[] height = new int[1];
            STBImage.stbi_info_from_memory(buffer, width, height, new int[1]);
            this.width = width[0];
            this.height = height[0];
        } catch (IOException ignored) {}
    }

    @Override
    public void render(HudRenderer renderer) {
        if (firstRender) {
            update();
            firstRender = false;
        }

        if (textureFetcher != null && textureFetcher.isDone()) TEXTURE = textureFetcher.join().get();

        if (TEXTURE == null) renderEmpty(renderer);
        else {
            if (TEXTURE instanceof NativeImageBackedTexture nativeTexture) nativeTexture.upload();
            else TEXTURE.bindTexture();
        }

        Renderer2D.TEXTURE.begin();

        if (hFlip.get() && vFlip.get()) IRenderer2D.TEXTURE.tokyo$texQuadHVFlip(x, y, getWidth(), getHeight(), colorFilter.get());
        else if (hFlip.get()) IRenderer2D.TEXTURE.tokyo$texQuadHFlip(x, y, getWidth(), getHeight(), colorFilter.get());
        else if (vFlip.get()) IRenderer2D.TEXTURE.tokyo$texQuadVFlip(x, y, getWidth(), getHeight(), colorFilter.get());
        else Renderer2D.TEXTURE.texQuad(x, y, getWidth(), getHeight(), colorFilter.get());

        Renderer2D.TEXTURE.render(null);
    }

    private void renderEmpty(HudRenderer renderer) {
        renderer.line(x, y, x + getWidth(), y + getWidth(), Color.GRAY);
        renderer.line(x, y + getWidth(), x + getWidth(), y, Color.GRAY);
    }

    public enum Mode {
        Preset,
        Resource,
        Online
    }

    public enum Preset {
        Meteor("meteor-client:textures/meteor.png", 128, 128),
        LiveLeak("tokyo-client:textures/imagehud/liveleak.png"),
        DallE("tokyo-client:textures/imagehud/dalle.png"),
        HyperCam("tokyo-client:textures/imagehud/hypercam.png", 350, 30),
        Brazzers("tokyo-client:textures/imagehud/brazzers.png", 262, 50),
        ActivateWindows("tokyo-client:textures/imagehud/activate_windows.png");

        final Identifier identifier;
        final int width;
        final int height;

        Preset(String location) {
            this(location, -1, -1);
        }

        Preset(String location, int width, int height) {
            this.identifier = Identifier.tryParse(location);
            this.width = width;
            this.height = height;
        }

        public void updateSize(ImageHud element) {
            if (width != -1 && height != -1) {
                element.width = width;
                element.height = height;
            } else element.metaFromResource(mc.getResourceManager().getResource(identifier).orElseThrow());
            element.updateSize();
        }
    }
}
