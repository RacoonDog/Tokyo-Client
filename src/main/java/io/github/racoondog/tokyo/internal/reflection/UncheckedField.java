package io.github.racoondog.tokyo.internal.reflection;

import java.lang.reflect.Field;

public class UncheckedField<T> {
    public final Field field;
    private final Class<T> fieldType;

    public UncheckedField(Field field, Class<T> fieldType) {
        this.field = field;
        this.fieldType = fieldType;
    }

    public T getStatic() {
        try {
            return fieldType.cast(field.get(null));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public T get(Object instance) {
        try {
            return fieldType.cast(field.get(instance));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStatic(T value) {
        try {
            field.set(null, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object instance, T value) {
        try {
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
