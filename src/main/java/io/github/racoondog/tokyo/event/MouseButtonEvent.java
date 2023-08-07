package io.github.racoondog.tokyo.event;

public class MouseButtonEvent {
    private static final MouseButtonEvent INSTANCE = new MouseButtonEvent();

    public int button;
    public int action;
    public int modifiers;

    public static MouseButtonEvent get(int button, int action, int modifiers) {
        INSTANCE.button = button;
        INSTANCE.action = action;
        INSTANCE.modifiers = modifiers;
        return INSTANCE;
    }
}
