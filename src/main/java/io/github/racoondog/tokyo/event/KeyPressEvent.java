package io.github.racoondog.tokyo.event;

public class KeyPressEvent {
    private static final KeyPressEvent INSTANCE = new KeyPressEvent();

    public int key;
    public int scancode;
    public int action;
    public int modifiers;

    public static KeyPressEvent get(int key, int scancode, int action, int modifiers) {
        INSTANCE.key = key;
        INSTANCE.scancode = scancode;
        INSTANCE.action = action;
        INSTANCE.modifiers = modifiers;
        return INSTANCE;
    }
}
