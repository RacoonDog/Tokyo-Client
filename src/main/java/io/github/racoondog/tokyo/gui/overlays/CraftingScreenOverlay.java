package io.github.racoondog.tokyo.gui.overlays;

import io.github.racoondog.meteorsharedaddonutils.features.ScreenContainer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class CraftingScreenOverlay extends ScreenContainer {
    private WTable itemCountTable;

    public CraftingScreenOverlay() {
        super(GuiThemes.get());
    }

    @Override
    public void initWidgets() {
        WWindow window = this.add(theme.window(null, "Crafting Overlay")).widget();
        window.view.scrollOnlyWhenMouseOver = false;

        window.add(theme.label("Inventory Item Counts:")).expandX();
        window.add(theme.horizontalSeparator()).expandX();

        itemCountTable = window.add(theme.table()).expandX().widget();
    }

    public void onTick() {
        itemCountTable.clear();

        Object2IntMap<Item> inventoryItems = new Object2IntOpenHashMap<>();

        handleStack(inventoryItems, mc.player.getInventory().offHand.get(0));
        for (int i = SlotUtils.HOTBAR_START; i < SlotUtils.MAIN_END; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            handleStack(inventoryItems, stack);
        }

        if (inventoryItems.isEmpty()) itemCountTable.add(theme.label("Empty"));
        else {
            for (var entry : inventoryItems.object2IntEntrySet()) {
                itemCountTable.add(theme.label(I18n.translate(entry.getKey().getTranslationKey())));
                itemCountTable.add(theme.label(": "));
                itemCountTable.add(theme.label(String.valueOf(entry.getIntValue())));
                itemCountTable.row();
            }
        }
    }

    private static void handleStack(Object2IntMap<Item> map, ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.AIR) return;
        if (map.containsKey(item)) map.put(item, map.getInt(item) + stack.getCount());
        else map.put(item, stack.getCount());
    }
}
