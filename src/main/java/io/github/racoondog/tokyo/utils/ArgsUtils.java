package io.github.racoondog.tokyo.utils;

import io.github.racoondog.tokyo.LaunchHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class ArgsUtils {
    public static int indexOf(Object[] data, Object o) {
        return indexOf(data, o, 0, data.length);
    }

    /** Based on {@link java.util.ArrayList#indexOf(Object)} */
    public static int indexOf(Object[] data, Object o, int start, int end) {
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (data[i] == null) return i;
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(data[i])) return i;
            }
        }
        return -1;
    }

    public static String getArgOrElse(String argName, Supplier<String> supplier) {
        int argIdx = indexOf(LaunchHandler.LAUNCH_ARGS, argName);
        return argIdx == -1 || argIdx >= LaunchHandler.LAUNCH_ARGS.length ? supplier.get() : LaunchHandler.LAUNCH_ARGS[argIdx + 1];
    }

    public static void modifyArg(List<String> list, String token, String replace) {
        int idx = list.indexOf(token);
        if (idx != -1) {
            list.set(idx + 1, replace);
        } else {
            list.add(token);
            list.add(replace);
        }
    }
}
