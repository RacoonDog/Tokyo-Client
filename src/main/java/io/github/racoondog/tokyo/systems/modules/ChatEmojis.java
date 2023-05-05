package io.github.racoondog.tokyo.systems.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.systems.modules.Module;
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
import java.util.*;
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

                registerBuiltIn(":blushib:", 96, 96, 0, 0);
                registerBuiltIn(":catheart:", 96, 96, 96, 0);
                registerBuiltIn(":concern:", 96, 96, 192, 0);
                registerBuiltIn(":cope:", 96, 96, 288, 0);
                registerBuiltIn(":OoO:", 96, 96, 0, 96);
                registerBuiltIn(":pepelaugh:", 96, 96, 96, 96);
                registerBuiltIn(":prayge:", 96, 96, 192, 96);
                registerBuiltIn(":shock:", 96, 96, 288, 96);
                registerBuiltIn(":fancytroll:", 96, 96, 0, 192);
                registerBuiltIn(":goodjob:", 96, 96, 96, 192);
                registerBuiltIn(":hmmm:", 96, 96, 192, 192);
                registerBuiltIn(":neutraltroll:", 96, 96, 288, 192);
                registerBuiltIn(":skull:", 96, 96, 0, 288);
                registerBuiltIn(":trolleyes:", 96, 96, 96, 288);
                registerBuiltIn(":troll:", 96, 96, 192, 288);
                registerBuiltIn(":trollswagcat:", 96, 96, 288, 288);

                for (var resourcePair : manager.findResources("textures/tokyo-chat-emojis", path -> path.getPath().endsWith(".png")).entrySet()) {
                    Emoji emoji = Emoji.fromTexture(resourcePair.getKey(), resourcePair.getValue());
                    StringBuilder emojiNameBuilder = new StringBuilder(":");
                    String path = resourcePair.getKey().getPath();
                    int slashIdx = path.lastIndexOf('/');
                    emojiNameBuilder.append(path, slashIdx == -1 ? 0 : slashIdx + 1, path.length() - 4);
                    emojiNameBuilder.append(':');
                    register(emojiNameBuilder.toString(), emoji);
                }

                for (var uhh : REGISTRY.keySet()) {
                    System.out.println(uhh);
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

    public static Collection<String> getEmojiNames() {
        return REGISTRY.keySet();
    }

    public static boolean shouldRender() {
        return INSTANCE.isActive();
    }

    private static void registerBuiltIn(String name, int width, int height, int u, int v) {
        register(name, Emoji.fromAtlas(BUILTIN_EMOJI_ATLAS_ID, width, height, u, v));
    }

    public static final class Emoji {
        private static Emoji missing;
        private final AbstractTexture texture;
        private final int texWidth;
        private final int texHeight;
        private final int u;
        private final int v;
        private final int regWidth;
        private final int regHeight;

        public Emoji(AbstractTexture texture, int texWidth, int texHeight, int u, int v, int regWidth, int regHeight) {
            this.texture = texture;
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.u = u;
            this.v = v;
            this.regWidth = regWidth;
            this.regHeight = regHeight;
        }

        public Emoji(AbstractTexture texture, int texWidth, int texHeight) {
            this(texture, texWidth, texHeight, 0, 0, texWidth, texHeight);
        }

        public void render(MatrixStack matrices, int x, int y, int fontHeight) {
            if (texture instanceof NativeImageBackedTexture nativeImageBackedTexture) nativeImageBackedTexture.upload();
            else texture.bindTexture();

            RenderSystem._setShaderTexture(0, texture.getGlId());

            int ratio = Math.max(texHeight, texWidth) / fontHeight;

            int width = texWidth / ratio;
            int height = texHeight / ratio;

            DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regWidth, regHeight, texWidth, texHeight);
        }

        public static Emoji fromAtlas(Identifier atlasId, int width, int height, int u, int v) {
            Optional<Resource> atlasResource = MinecraftClient.getInstance().getResourceManager().getResource(atlasId);

            if (atlasResource.isPresent()) {
                try (var inputStream = atlasResource.get().getInputStream();
                     var nativeImage = NativeImage.read(inputStream)) {
                    return new Emoji(MinecraftClient.getInstance().getTextureManager().getTexture(atlasId), nativeImage.getWidth(), nativeImage.getHeight(), u, v, width, height);
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

        public static Emoji fromNativeImage(NativeImage nativeImage) {
            return new Emoji(new NativeImageBackedTexture(nativeImage), nativeImage.getWidth(), nativeImage.getHeight());
        }

        public static Emoji fromNativeImageBackedTexture(NativeImageBackedTexture texture) {
            return new Emoji(texture, texture.getImage().getWidth(), texture.getImage().getHeight());
        }

        public static Emoji getMissing() {
            if (missing == null)
                missing = new Emoji(MissingSprite.getMissingSpriteTexture(), MissingSprite.getMissingSpriteTexture().getImage().getWidth(), MissingSprite.getMissingSpriteTexture().getImage().getHeight());
            return missing;
        }
    }
}
