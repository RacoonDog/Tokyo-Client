package io.github.racoondog.tokyo.internal.reflection;

import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

import java.lang.constant.Constable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.function.Function;

@ApiStatus.Internal
public final class Reflection {
    private static final Unsafe UNSAFE;
    private static final MethodHandles.Lookup TRUSTED_LOOKUP;

    private Reflection() {}

    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe) unsafeField.get(null);

            MethodHandles.lookup();
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            long lookupFieldOffset = UNSAFE.staticFieldOffset(lookupField);
            TRUSTED_LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(MethodHandles.Lookup.class, lookupFieldOffset);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Constable, E extends ReflectiveOperationException> T lookup(ReflectiveOperation<T, E> operation) {
        return lookup(operation, Reflection::rethrow);
    }

    public static <T extends Constable, E extends ReflectiveOperationException> T lookup(ReflectiveOperation<T, E> operation, Function<ReflectiveOperationException, T> errorHandler) {
        try {
            return operation.lookup(TRUSTED_LOOKUP);
        } catch (ReflectiveOperationException e) {
            return errorHandler.apply(e);
        }
    }

    private static <T, E extends Throwable> T rethrow(E throwable) {
        UNSAFE.throwException(throwable);
        return null;
    }

    @FunctionalInterface
    public interface ReflectiveOperation<T extends Constable, E extends ReflectiveOperationException> {
        T lookup(MethodHandles.Lookup lookup) throws E;
    }
}
