package io.github.racoondog.tokyo.mixin.overlays;

import io.github.racoondog.meteorsharedaddonutils.features.ScreenContainer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
@Mixin(CraftingScreen.class)
public abstract class CraftingScreenMixin extends HandledScreen<CraftingScreenHandler> {
    private ScreenContainer container;
    private WTable table;

    private CraftingScreenMixin(CraftingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(CraftingScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        container = new ScreenContainer(GuiThemes.get()) {
            @Override
            public void initWidgets() {
                WWindow window = this.add(theme.window(null, "Crafting Overlay")).widget();
                window.view.scrollOnlyWhenMouseOver = false;

                window.add(theme.label("Inventory Item Counts:")).expandX();
                window.add(theme.horizontalSeparator()).expandX();

                table = window.add(theme.table()).expandX().widget();
            }
        };
    }

    @Inject(method = "handledScreenTick", at = @At("TAIL"))
    private void injectTick(CallbackInfo ci) {
        table.clear();

        Object2IntMap<Item> inventoryItems = new Object2IntOpenHashMap<>();

        handleStack(inventoryItems, mc.player.getInventory().offHand.get(0));
        for (int i = SlotUtils.HOTBAR_START; i < SlotUtils.MAIN_END; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            handleStack(inventoryItems, stack);
        }

        if (inventoryItems.isEmpty()) table.add(container.theme.label("Empty"));
        else {
            for (var entry : inventoryItems.object2IntEntrySet()) {
                table.add(container.theme.label(I18n.translate(entry.getKey().getTranslationKey())));
                table.add(container.theme.label(": "));
                table.add(container.theme.label(String.valueOf(entry.getIntValue())));
                table.row();
            }
        }
    }

    @Unique
    private static void handleStack(Object2IntMap<Item> map, ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.AIR) return;
        if (map.containsKey(item)) map.put(item, map.getInt(item) + stack.getCount());
        else map.put(item, stack.getCount());
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void injectInit(CallbackInfo ci) {
        container.init();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void injectMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (container.mouseClicked(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (container.mouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        container.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (container.mouseScrolled(mouseX, mouseY, amount)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (container.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (container.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (container.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void injectRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        container.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        container.resize(client, width, height);
        super.resize(client, width, height);
    }

    @Override
    public void removed() {
        container.removed();
        super.removed();
    }
}
