package io.github.racoondog.tokyo.utils.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class ReflectionUtils {
    private static final Int2ObjectMap<Class<?>> classHashCache = new Int2ObjectOpenHashMap<>();

    @Nullable
    public static Class<?> getInnerClass(Class<?> outerClass) {
        Class<?>[] classes = outerClass.getDeclaredClasses();
        if (classes.length != 0) return classes[0];
        return null;
    }

    @Nullable
    public static Class<?> getInnerClass(Class<?> outerClass, String name) {
        byte type = 0; //getSimpleName()
        if (name.contains(".")) type = 1; //getName();
        else if (name.contains("$")) type = 2; //getCanonicalName();
        for (var innerClass : outerClass.getDeclaredClasses()) {
            String innerClassName = switch (type) {
                case 1 -> innerClass.getName();
                case 2 -> innerClass.getCanonicalName();
                default -> innerClass.getSimpleName();
            };
            if (name.equals(innerClassName)) return innerClass;
        }
        return null;
    }

    @Nullable
    public static Class<?> c_getInnerClass(Class<?> outerClass, String name) {
        int hash = Objects.hash(outerClass, name);
        return classHashCache.computeIfAbsent(hash, k -> getInnerClass(outerClass, name));
    }

    @Nullable
    public static Field getField(Class<?> sourceClass, String name) {
        try {
            Field f = sourceClass.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static Cache cache() {
        return new Cache();
    }

    public static Cache cache(int size) {
        return new Cache(size);
    }

    public static class Cache {
        private final Map<String, Field> fieldCache;

        private Cache() {
            fieldCache = new HashMap<>();
        }

        private Cache(int size) {
            fieldCache = new HashMap<>(size);
        }

        @Nullable
        public Field getField(Class<?> clazz, String fieldName) {
            return fieldCache.computeIfAbsent(fieldName, k -> ReflectionUtils.getField(clazz, k));
        }
    }
}
