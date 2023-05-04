package io.github.racoondog.tokyo.systems.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.PreInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ChatEmojis extends Module {
    public static final ChatEmojis INSTANCE = new ChatEmojis();
    public static final Pattern EMOJI_REGEX = Pattern.compile("(:[a-z0-9._-]+:)");
    public static final Map<String, Emoji> REGISTRY = new HashMap<>();
    private static final Identifier BUILTIN_EMOJI_ATLAS_ID = new Identifier(Tokyo.MOD_ID, "textures/builtin-chat-emojis.png");

    public static void earlyInit() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(Tokyo.MOD_ID, "chat_emojis_resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                for (var oldResource : REGISTRY.values()) {
                    oldResource.texture.close();
                }
                REGISTRY.clear();

                for (var resourcePair : manager.findResources("textures/tokyo-chat-emojis", path -> path.getPath().endsWith(".png")).entrySet()) {
                    Emoji emoji = Emoji.fromTexture(resourcePair.getKey(), resourcePair.getValue());
                    StringBuilder emojiNameBuilder = new StringBuilder(":");
                    String path = resourcePair.getKey().getPath();
                    int slashIdx = path.lastIndexOf('/');
                    emojiNameBuilder.append(path, slashIdx == -1 ? 0 : slashIdx + 1, path.length() - 4);
                    emojiNameBuilder.append(':');
                    REGISTRY.put(emojiNameBuilder.toString(), emoji);
                }
            }
        });
    }

    private ChatEmojis() {
        super(Tokyo.CATEGORY, "chat-emojis", "");
    }

    @Nullable
    public static Emoji get(String emojiName) {
        return REGISTRY.get(emojiName);
    }

    public static void register(String emojiName, Emoji emoji) {
        REGISTRY.put(emojiName, emoji);
    }

    public static boolean shouldRender() {
        return INSTANCE.isActive();
    }

    private void registerBuiltIn(String name, int width, int height, int u, int v) {
        REGISTRY.put(name, Emoji.fromAtlas(BUILTIN_EMOJI_ATLAS_ID, width, height, u, v));
    }

    public record Emoji(AbstractTexture texture, int texWidth, int texHeight, int u, int v, int regWidth, int regHeight) {
        private static Emoji missing;

        public void render(MatrixStack matrices, int x, int y, int fontHeight) {
            if (texture instanceof NativeImageBackedTexture nativeImageBackedTexture) nativeImageBackedTexture.upload();
            else texture.bindTexture();

            RenderSystem._setShaderTexture(0, texture.getGlId());

            int ratio = Math.max(texHeight, texWidth) / fontHeight;

            int width = texWidth / ratio;
            int height = texHeight / ratio;

            DrawableHelper.drawTexture(matrices, x, y, this.u, this.v, width, height, width, height);
        }

        public static Emoji fromAtlas(Identifier atlasId, int width, int height, int u, int v) {
            Optional<Resource> atlasResource = MinecraftClient.getInstance().getResourceManager().getResource(atlasId);

            if (atlasResource.isPresent()) {
                try (var inputStream = atlasResource.get().getInputStream();
                     var nativeImage = NativeImage.read(inputStream)) {
                    return new Emoji(MinecraftClient.getInstance().getTextureManager().getTexture(atlasId), nativeImage.getWidth(), nativeImage.getHeight(), 0, 0, width, height);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return getMissing();
        }

        public static Emoji fromTexture(Identifier textureId, Resource resource) {
            try (var inputStream = resource.getInputStream();
                 var nativeImage = NativeImage.read(inputStream)) {
                return new Emoji(MinecraftClient.getInstance().getTextureManager().getTexture(textureId), nativeImage.getWidth(), nativeImage.getHeight(), 0, 0, nativeImage.getWidth(), nativeImage.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return getMissing();
        }

        private static Emoji getMissing() {
            if (missing == null)
                missing = new Emoji(MissingSprite.getMissingSpriteTexture(), MissingSprite.getMissingSpriteTexture().getImage().getWidth(), MissingSprite.getMissingSpriteTexture().getImage().getHeight(), 0, 0, MissingSprite.getMissingSpriteTexture().getImage().getWidth(), MissingSprite.getMissingSpriteTexture().getImage().getHeight());
            return missing;
        }
    }
}
