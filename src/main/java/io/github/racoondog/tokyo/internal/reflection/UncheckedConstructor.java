package io.github.racoondog.tokyo.internal.reflection;

import java.lang.reflect.Constructor;

public class UncheckedConstructor<T> {
    public final Constructor<T> ctor;

    public UncheckedConstructor(Constructor<T> ctor) {
        this.ctor = ctor;
    }

    public T newInstance(Object... args) {
        try {
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
