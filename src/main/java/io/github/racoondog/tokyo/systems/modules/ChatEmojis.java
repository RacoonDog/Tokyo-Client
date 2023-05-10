package io.github.racoondog.tokyo.systems.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.racoondog.tokyo.Tokyo;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
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
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ChatEmojis extends Module {
    public static final ChatEmojis INSTANCE = new ChatEmojis();
    public static final Pattern EMOJI_REGEX = Pattern.compile("(:[a-z0-9._-]+:)");
    private static final Map<String, Emoji> REGISTRY = new HashMap<>();
    private static final List<AnimatedEmoji> TICKABLE_EMOJIS = new ArrayList<>();
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
                    String emojiName = nameFromIdentifier(resourcePair.getKey());

                    Optional<Resource> metadata = MinecraftClient.getInstance().getResourceManager().getResource(resourcePair.getKey().withSuffixedPath(".mcmeta"));

                    if (metadata.isPresent()) register(emojiName, AnimatedEmoji.fromResource(resourcePair.getKey(), resourcePair.getValue(), metadata.get()));
                    else register(emojiName, Emoji.fromResource(resourcePair.getKey(), resourcePair.getValue()));
                }
            }
        });
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        TICKABLE_EMOJIS.forEach(AnimatedEmoji::tick);
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
        if (emoji instanceof AnimatedEmoji animatedEmoji) TICKABLE_EMOJIS.add(animatedEmoji);
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

    private static String nameFromIdentifier(Identifier id) {
        StringBuilder sb = new StringBuilder(":");
        int slashIdx = id.getPath().lastIndexOf('/');
        sb.append(id.getPath(), slashIdx == -1 ? 0 : slashIdx + 1, id.getPath().length() - 4);
        sb.append(':');
        return sb.toString();
    }

    public static class Emoji {
        private static Emoji missing;
        protected final AbstractTexture texture;
        protected final int texWidth;
        protected final int texHeight;
        private final int u;
        private final int v;
        protected final int regWidth;
        protected final int regHeight;

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

        public static Emoji fromResource(Identifier textureId, Resource resource) {
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

    public static class AnimatedEmoji extends Emoji {
        private static final Gson GSON = new GsonBuilder().create();
        private final int[] frameMap;
        private final int regionsHeight;
        private int regionIndex = 0;

        public AnimatedEmoji(AbstractTexture texture, int texWidth, int texHeight, int u, int v, int regWidth, int regHeight, int[] frameMap, int regionsHeight) {
            super(texture, texWidth, texHeight, u, v, regWidth, regHeight);
            this.frameMap = frameMap;
            this.regionsHeight = regionsHeight;
        }

        /**
         * uhh
         * @author Crosby
         */
        public static AnimatedEmoji fromResource(Identifier textureId, Resource texture, Resource metadata) {
            try (var inputStream = texture.getInputStream();
                 var nativeImage = NativeImage.read(inputStream);
                 var metadataReader = new InputStreamReader(metadata.getInputStream())) {

                int textureWidth = nativeImage.getWidth();
                int textureHeight = nativeImage.getHeight();

                JsonObject metaJson = GSON.fromJson(metadataReader, JsonObject.class);
                metaJson = metaJson.getAsJsonObject("animation");

                boolean landscape = textureWidth > textureHeight;
                int width = metaJson.has("width") ? metaJson.getAsJsonPrimitive("width").getAsInt() : (landscape ? textureHeight : textureWidth);
                int height = metaJson.has("height") ? metaJson.getAsJsonPrimitive("height").getAsInt() : (landscape ? textureWidth : textureHeight);

                if (textureWidth % width != 0) throw new IOException("Error loading %s: Texture width (%s) is not divisible by region width (%s).".formatted(textureId, textureWidth, width));
                if (textureHeight % height != 0) throw new IOException("Error loading %s: Texture height (%s) is not divisible by region height (%s).".formatted(textureId, textureHeight, height));

                int frameTime = metaJson.has("frametime") ? metaJson.getAsJsonPrimitive("frametime").getAsInt() : 1;

                int regionsWidth = textureWidth / width;
                int regionsHeight = textureHeight / width;
                int frameCount = regionsWidth * regionsHeight;

                int[] frames;

                if (metaJson.has("frames")) {
                    JsonArray frameJsonArray = metaJson.getAsJsonArray("frames");
                    IntList frameMap = new IntArrayList();

                    for (var frameJson : frameJsonArray) {
                        int frameIndex = frameJson.isJsonObject() ? frameJson.getAsJsonObject().getAsJsonPrimitive("index").getAsInt() : frameJson.getAsInt();
                        int time = frameJson.isJsonObject() ? frameJson.getAsJsonObject().getAsJsonPrimitive("time").getAsInt() : frameTime;

                        if (frameIndex > frameCount) throw new IOException("Error loading %s: Frame index (%s) is higher than frame count (%s).".formatted(textureId, frameIndex, frameCount));

                        for (int i = 0; i < time; i++) {
                            frameMap.add(frameIndex);
                        }
                    }

                    frames = frameMap.toIntArray();
                } else {
                    frames = new int[frameCount * frameTime];
                    int index = 0;
                    for (int i = 0; i < frameCount; i++) {
                        for (int j = 0; j < frameTime; j++) {
                            frames[index++] = i;
                        }
                    }
                }

                return new AnimatedEmoji(MinecraftClient.getInstance().getTextureManager().getTexture(textureId), nativeImage.getWidth(), nativeImage.getHeight(), 0, 0, width, height, frames, regionsHeight);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }

            return new AnimatedEmoji(MissingSprite.getMissingSpriteTexture(), MissingSprite.getMissingSpriteTexture().getImage().getWidth(), MissingSprite.getMissingSpriteTexture().getImage().getHeight(), 0, 0, MissingSprite.getMissingSpriteTexture().getImage().getWidth(), MissingSprite.getMissingSpriteTexture().getImage().getHeight(), new int[]{0}, 0); //cope
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int fontHeight) {
            if (texture instanceof NativeImageBackedTexture nativeImageBackedTexture) nativeImageBackedTexture.upload();
            else texture.bindTexture();

            RenderSystem._setShaderTexture(0, texture.getGlId());

            int ratio = Math.max(texHeight, texWidth) / fontHeight;

            int width = texWidth / ratio;
            int height = texHeight / ratio;

            int frameIndex = frameMap[regionIndex];
            int u = frameIndex / regionsHeight * regWidth;
            int v = frameIndex % regionsHeight * regHeight;

            DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regWidth, regHeight, texWidth, texHeight);
        }

        public void tick() {
            if (++regionIndex == frameMap.length - 1) regionIndex = 0;
        }
    }
}
