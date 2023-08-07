package io.github.racoondog.tokyo.event;

public class MouseScrollEvent {
    private static final MouseScrollEvent INSTANCE = new MouseScrollEvent();

    public double horizontalScroll;
    public double verticalScroll;

    public static MouseScrollEvent get(double horizontalScroll, double verticalScroll) {
        INSTANCE.horizontalScroll = horizontalScroll;
        INSTANCE.verticalScroll = verticalScroll;
        return INSTANCE;
    }
}
