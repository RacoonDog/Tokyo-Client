package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.internal.reflection.Reflection;
import io.github.racoondog.tokyo.internal.reflection.UncheckedConstructor;
import io.github.racoondog.tokyo.internal.reflection.UncheckedField;
import io.github.racoondog.tokyo.internal.reflection.UncheckedIntField;
import io.github.racoondog.tokyo.systems.modules.Prefix;
import io.github.racoondog.tokyo.utils.AddonHelper;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(value = TitleScreenCredits.class, remap = false)
public abstract class TitleScreenCreditsMixin {
    @Shadow private static @Final List<?> credits;
    @Shadow @Final private static int GRAY;
    @Shadow @Final private static int WHITE;

    @Shadow private static void init() {throw new AssertionError();}

    @Unique private static final Class<?> CREDIT_CLASS = Reflection.forName("meteordevelopment.meteorclient.utils.player.TitleScreenCredits$Credit");
    @Unique private static final Class<?> SECTION_CLASS = Reflection.forName("meteordevelopment.meteorclient.utils.player.TitleScreenCredits$Section");
    @Unique private static final UncheckedIntField CREDIT_WIDTH = Reflection.uncheckedInt(Reflection.getPrivateField(CREDIT_CLASS, "width"));
    @Unique private static final UncheckedField<List> CREDIT_SECTIONS = Reflection.unchecked(Reflection.getPrivateField(CREDIT_CLASS, "sections"), List.class);
    @Unique private static final UncheckedField<MeteorAddon> CREDIT_ADDON = Reflection.unchecked(Reflection.getPrivateField(CREDIT_CLASS, "addon"), MeteorAddon.class);
    @Unique private static final UncheckedIntField SECTION_WIDTH = Reflection.uncheckedInt(Reflection.getPrivateField(SECTION_CLASS, "width"));
    @Unique private static final UncheckedField<String> SECTION_TEXT = Reflection.unchecked(Reflection.getPrivateField(SECTION_CLASS, "text"), String.class);
    @Unique private static final UncheckedIntField SECTION_COLOR = Reflection.uncheckedInt(Reflection.getPrivateField(SECTION_CLASS, "color"));

    @Unique private static final MutableText tokyoText = Text.literal("(").formatted(Formatting.GRAY).append(Text.literal(Tokyo.CONTAINER.getMetadata().getVersion().getFriendlyString()).formatted(Formatting.WHITE)).append(") by ");
    @Unique private static int previousWidth;

    static {
        for (int i = 0; i < Tokyo.INSTANCE.authors.length; i++) {
            if (i > 0) tokyoText.append(Text.literal(i == Tokyo.INSTANCE.authors.length - 1 ? " & " : ", "));
            tokyoText.append(Text.literal(Tokyo.INSTANCE.authors[i]).formatted(Formatting.WHITE));
        }

        previousWidth = MinecraftClient.getInstance().textRenderer.getWidth(Prefix.getTokyo()) + MinecraftClient.getInstance().textRenderer.getWidth(tokyoText);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"))
    private static void modifyCredits(CallbackInfo ci) {
        UncheckedConstructor<?> sectionCtor = Reflection.unchecked(Reflection.getPrivateCtor(SECTION_CLASS, String.class, int.class));

        for (var credit : credits) {
            MeteorAddon addon = CREDIT_ADDON.get(credit);
            List<Object> sections = (List<Object>) CREDIT_SECTIONS.get(credit);

            if (addon != Tokyo.INSTANCE) {
                Optional<ModMetadata> optionalMeta = AddonHelper.getMeta(addon);
                if (optionalMeta.isEmpty()) {
                    Tokyo.LOG.warn("Couldn't find mod container for '{}'.", addon.name);
                    continue;
                }

                String versionString = optionalMeta.get().getVersion().getFriendlyString();

                sections.add(1, sectionCtor.newInstance(" (", GRAY));
                sections.add(2, sectionCtor.newInstance(versionString, WHITE));
                sections.add(3, sectionCtor.newInstance(")", GRAY));
            } else {
                CREDIT_WIDTH.set(credit, previousWidth);
            }
        }
    }

    /**
     * @author Crosby
     * @reason I hate this
     */
    @SuppressWarnings("unchecked")
    @Overwrite
    public static void render(DrawContext context) {
        if (credits.isEmpty()) init();

        int y = 3;
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        for (var credit : credits) {
            if (CREDIT_ADDON.get(credit) == Tokyo.INSTANCE) {
                if (previousWidth != CREDIT_WIDTH.get(credit)) {
                    previousWidth = renderer.getWidth(Prefix.getTokyo()) + renderer.getWidth(tokyoText);
                    CREDIT_WIDTH.set(credit, previousWidth);
                    credits.sort(Comparator.comparingInt(value -> CREDIT_ADDON.get(value) == MeteorClient.ADDON ? Integer.MIN_VALUE : -CREDIT_WIDTH.get(value)));
                }

                int x = MinecraftClient.getInstance().currentScreen.width - 3 - previousWidth;

                context.drawTextWithShadow(renderer, Text.empty().append(Prefix.getTokyo()).append(tokyoText), x, y, -1);

                continue;
            } else {
                int x = MinecraftClient.getInstance().currentScreen.width - 3 - CREDIT_WIDTH.get(credit);
                List<Object> sections = (List<Object>) CREDIT_SECTIONS.get(credit);

                synchronized (sections) {
                    for (var section : sections) {
                        context.drawTextWithShadow(renderer, SECTION_TEXT.get(section), x, y, SECTION_COLOR.get(section));
                        x += SECTION_WIDTH.get(section);
                    }
                }
            }

            y += renderer.fontHeight + 2;
        }
    }
}
