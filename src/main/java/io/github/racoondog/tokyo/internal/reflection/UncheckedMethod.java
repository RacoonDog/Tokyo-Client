package io.github.racoondog.tokyo.internal.reflection;

import java.lang.reflect.Method;

public class UncheckedMethod<T> {
    public final Method method;
    private final Class<T> returnType;

    public UncheckedMethod(Method method, Class<T> returnType) {
        this.method = method;
        this.returnType = returnType;
    }

    public T invokeStatic(Object... args) {
        try {
            return returnType.cast(method.invoke(null, args));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public T invoke(Object instance, Object... args) {
        try {
            return returnType.cast(method.invoke(instance, args));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
