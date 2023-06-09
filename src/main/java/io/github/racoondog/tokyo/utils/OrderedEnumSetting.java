package io.github.racoondog.tokyo.utils;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OrderedEnumSetting<T extends Enum<T>> extends Setting<List<T>> {
    private final T[] values;

    @SuppressWarnings("unchecked")
    public OrderedEnumSetting(String name, String description, Consumer<List<T>> onChanged, Consumer<Setting<List<T>>> onModuleActivated, IVisible visible, T... defaultOrder) {
        super(name, description, new ArrayList<>(List.of(defaultOrder)), onChanged, onModuleActivated, visible);

        try {
            values = (T[]) defaultOrder[0].getClass().getMethod("values").invoke(null);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    @Override
    protected List<T> parseImpl(String str) {
        List<T> list = new ArrayList<>();

        for (var val : str.split(",")) {
            list.add(getMatchingEnum(val));
        }

        return list;
    }

    @Override
    protected boolean isValueValid(List<T> value) {
        return true;
    }

    @Override
    protected NbtCompound save(NbtCompound tag) {
        NbtList list = new NbtList();

        for (var value : values) {
            list.add(NbtString.of(value.name()));
        }

        tag.put("values", list);

        return tag;
    }

    @Override
    protected List<T> load(NbtCompound tag) {
        value.clear();
        for (var enumValue : tag.getList("values", NbtElement.STRING_TYPE)) {
            value.add(getMatchingEnum(enumValue.asString()));
        }

        return value;
    }

    @Nullable
    private T getMatchingEnum(String name) {
        for (var value : values) {
            if (name.equalsIgnoreCase(value.name())) return value;
        }
        return null;
    }

    public static class Builder<T extends Enum<T>> extends SettingBuilder<OrderedEnumSetting.Builder<T>, List<T>, OrderedEnumSetting<T>> {
        private T[] defaultOrder;

        public Builder() {
            super(null);
        }

        @SafeVarargs
        public final Builder<T> defaultValue(T... defaults) {
            this.defaultOrder = defaults;
            return this;
        }

        @SuppressWarnings({"unchecked", "ConstantConditions"})
        @Override
        public Builder<T> defaultValue(List<T> defaultValue) {
            this.defaultOrder = defaultValue.toArray((T[]) new Object[0]);
            return this;
        }

        @Override
        public OrderedEnumSetting<T> build() {
            return new OrderedEnumSetting<>(name, description, onChanged, onModuleActivated, visible, defaultOrder);
        }
    }
}
