package io.github.racoondog.tokyo.gui;

import io.github.racoondog.tokyo.utils.settings.OrderedEnumSetting;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.PreInit;

import java.util.Collections;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class OrderedEnumSettingScreen<T extends Enum<T>> extends WindowScreen {
    private final OrderedEnumSetting<T> setting;
    private final WVerticalList list;

    @PreInit
    public static void preInit() {
        SettingsWidgetFactory.registerCustomFactory(OrderedEnumSetting.class, theme -> (table, setting) -> {
            OrderedEnumSetting<?> orderedEnumSetting = (OrderedEnumSetting<?>) setting;

            WButton button = table.add(theme.button("Edit")).expandCellX().widget();
            button.action = () -> mc.setScreen(new OrderedEnumSettingScreen<>(theme, orderedEnumSetting));

            WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
            reset.action = setting::reset;
        });
    }

    public OrderedEnumSettingScreen(GuiTheme theme, OrderedEnumSetting<T> setting) {
        super(theme, setting.title);
        this.setting = setting;

        list = super.add(theme.verticalList()).expandX().widget();
    }

    @Override
    public <W extends WWidget> Cell<W> add(W widget) {
        return list.add(widget);
    }

    @Override
    public void initWidgets() {
        build();
    }

    private void build() {
        WTable table = add(theme.table()).expandX().widget();
        for (int i = 0; i < setting.get().size(); i++) {
            T value = setting.get().get(i);

            table.add(theme.label("%s. ".formatted(i + 1)));
            table.add(theme.label(value.name())).expandX();
            WVerticalList buttons = table.add(theme.verticalList()).expandX().right().widget();

            if (i < setting.get().size() - 1) {
                WButton down = buttons.add(theme.button("Down")).expandX().widget();
                down.action = () -> {
                    int currentIndex = setting.get().indexOf(value);
                    Collections.swap(setting.get(), currentIndex, currentIndex - 1);
                    list.clear();
                    build();
                };

                table.row();
                table.add(theme.horizontalSeparator()).expandX();
                table.row();
            }

            if (i != 0) {
                WButton up = buttons.add(theme.button("Up")).expandX().widget();
                up.action = () -> {
                    int currentIndex = setting.get().indexOf(value);
                    Collections.swap(setting.get(), currentIndex, currentIndex + 1);
                    list.clear();
                    build();
                };
            }
        }
    }
}
