package io.github.racoondog.tokyo.internal.reflection;

import java.lang.reflect.Field;

public class UncheckedIntField {
    public final Field field;

    public UncheckedIntField(Field field) {
        this.field = field;
    }

    public int getStatic() {
        try {
            return field.getInt(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public int get(Object instance) {
        try {
            return field.getInt(instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStatic(int value) {
        try {
            field.setInt(null, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object instance, int value) {
        try {
            field.setInt(instance, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
