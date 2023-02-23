package io.github.racoondog.tokyo.utils;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TableBuilder extends WTable {
    private final GuiTheme theme;
    private final WTable root;
    private boolean empty = true;

    public TableBuilder(GuiTheme theme) {
        this.theme = theme;
        root = theme.table();
    }

    public TableBuilder addSegment(String name, Consumer<TableBuilder> consumer) {
        if (!empty) {
            root.row();
            root.add(theme.horizontalSeparator(name)).expandX();
            root.row();
        }
        empty = false;

        segment(consumer);

        return this;
    }

    public TableBuilder addSegment(Consumer<TableBuilder> consumer) {
        if (!empty) {
            root.row();
            root.add(theme.horizontalSeparator()).expandX();
            root.row();
        }
        empty = false;

        segment(consumer);

        return this;
    }

    private void segment(Consumer<TableBuilder> consumer) {
        TableBuilder builder = new TableBuilder(theme);
        consumer.accept(builder);
        root.add(builder.build()).expandX();
    }

    public WTable build() {
        return root;
    }

    // WTable Wrapper


    @Override
    public <T extends WWidget> Cell<T> add(T widget) {
        return root.add(widget);
    }

    @Override
    public void row() {
        root.row();
    }
}
