package io.github.racoondog.tokyo.internal.reflection;

import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@ApiStatus.Internal
public final class Reflection {
    private Reflection() {}

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Constructor<T> getPrivateCtor(Class<T> targetClass, Class<?>... paramTypes) {
        try {
            Constructor<T> ctor = targetClass.getConstructor(paramTypes);
            ctor.setAccessible(true);
            return ctor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getPrivateMethod(Class<?> targetClass, String methodName, Class<?> paramTypes) {
        try {
            Method method = targetClass.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getPrivateField(Class<?> targetClass, String fieldName) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> UncheckedConstructor<T> unchecked(Constructor<T> ctor) {
        return new UncheckedConstructor<>(ctor);
    }

    public static <T> UncheckedField<T> unchecked(Field field, Class<T> fieldType) {
        return new UncheckedField<>(field, fieldType);
    }

    public static UncheckedIntField uncheckedInt(Field field) {
        return new UncheckedIntField(field);
    }

    public static <T> UncheckedMethod<T> unchecked(Method method, Class<T> returnType) {
        return new UncheckedMethod<>(method, returnType);
    }
}
