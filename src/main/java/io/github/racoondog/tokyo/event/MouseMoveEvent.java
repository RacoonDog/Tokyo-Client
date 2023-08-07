package io.github.racoondog.tokyo.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MouseMoveEvent {
    private static final MouseMoveEvent INSTANCE = new MouseMoveEvent();

    public double x;
    public double y;

    public static MouseMoveEvent get(double x, double y) {
        INSTANCE.x = x;
        INSTANCE.y = y;
        return INSTANCE;
    }
}
