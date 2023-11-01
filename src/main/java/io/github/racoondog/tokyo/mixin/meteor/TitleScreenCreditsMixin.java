package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.internal.reflection.Reflection;
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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.AbstractList;
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
    @Unique private static final VarHandle CREDIT_WIDTH = Reflection.lookup(lookup -> lookup.findVarHandle(CREDIT_CLASS, "width", int.class));
    @Unique private static final VarHandle CREDIT_SECTIONS = Reflection.lookup(lookup -> lookup.findVarHandle(CREDIT_CLASS, "sections", List.class));
    @Unique private static final VarHandle CREDIT_ADDON = Reflection.lookup(lookup -> lookup.findVarHandle(CREDIT_CLASS, "addon", MeteorAddon.class));
    @Unique private static final VarHandle SECTION_WIDTH = Reflection.lookup(lookup -> lookup.findVarHandle(SECTION_CLASS, "width", int.class));
    @Unique private static final VarHandle SECTION_TEXT = Reflection.lookup(lookup -> lookup.findVarHandle(SECTION_CLASS, "text", String.class));
    @Unique private static final VarHandle SECTION_COLOR = Reflection.lookup(lookup -> lookup.findVarHandle(SECTION_CLASS, "color", int.class));
    @Unique private static final VarHandle ABSTRACT_LIST_MODCOUNT = Reflection.lookup(lookup -> lookup.findVarHandle(AbstractList.class, "modCount", int.class));

    @Unique private static final MutableText tokyoAuthorText = Text.literal("(").formatted(Formatting.GRAY).append(Text.literal(Tokyo.CONTAINER.getMetadata().getVersion().getFriendlyString()).formatted(Formatting.WHITE)).append(") by ");
    @Unique private static Object tokyoCredit;

    static {
        for (int i = 0; i < Tokyo.INSTANCE.authors.length; i++) {
            if (i > 0) tokyoAuthorText.append(Text.literal(i == Tokyo.INSTANCE.authors.length - 1 ? " & " : ", "));
            tokyoAuthorText.append(Text.literal(Tokyo.INSTANCE.authors[i]).formatted(Formatting.WHITE));
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"))
    private static void modifyCredits(CallbackInfo ci) {
        MethodHandle sectionConstructor = Reflection.lookup(lookup -> lookup.findConstructor(SECTION_CLASS, MethodType.methodType(void.class, String.class, int.class)));

        for (var credit : credits) {
            MeteorAddon addon = (MeteorAddon) CREDIT_ADDON.get(credit);
            List<Object> sections = (List<Object>) CREDIT_SECTIONS.get(credit);

            if (addon != Tokyo.INSTANCE) {
                Optional<ModMetadata> optionalMeta = AddonHelper.getMeta(addon);
                if (optionalMeta.isEmpty()) {
                    Tokyo.LOG.warn("Couldn't find mod container for '{}'.", addon.name);
                    continue;
                }

                String versionString = optionalMeta.get().getVersion().getFriendlyString();

                try {
                    sections.add(1, sectionConstructor.invokeExact(" (", GRAY));
                    sections.add(2, sectionConstructor.invokeExact(versionString, WHITE));
                    sections.add(3, sectionConstructor.invokeExact(")", GRAY));
                } catch (Throwable ignored) {}
            } else tokyoCredit = credit;
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

        Text tokyoText = Text.empty().append(Prefix.getInnerTokyo()).append(" ").append(tokyoAuthorText);

        int tokyoWidth = renderer.getWidth(tokyoText);

        if (tokyoWidth != (int) CREDIT_WIDTH.get(tokyoCredit)) {
            CREDIT_WIDTH.set(tokyoCredit, tokyoWidth);
            int modCount = (int) ABSTRACT_LIST_MODCOUNT.get(credits); //prevent error
            credits.sort(Comparator.comparingInt(value -> CREDIT_ADDON.get(value) == MeteorClient.ADDON ? Integer.MIN_VALUE : -(int) CREDIT_WIDTH.get(value)));
            ABSTRACT_LIST_MODCOUNT.set(credits, modCount);
        }

        for (var credit : credits) {
            if (CREDIT_ADDON.get(credit) == Tokyo.INSTANCE) {
                int x = MinecraftClient.getInstance().currentScreen.width - 3 - tokyoWidth;

                context.drawTextWithShadow(renderer, tokyoText, x, y, -1);

                continue;
            } else {
                int x = MinecraftClient.getInstance().currentScreen.width - 3 - (int) CREDIT_WIDTH.get(credit);
                List<Object> sections = (List<Object>) CREDIT_SECTIONS.get(credit);

                synchronized (sections) {
                    for (var section : sections) {
                        context.drawTextWithShadow(renderer, (String) SECTION_TEXT.get(section), x, y, (int) SECTION_COLOR.get(section));
                        x += (int) SECTION_WIDTH.get(section);
                    }
                }
            }

            y += renderer.fontHeight + 2;
        }
    }
}
