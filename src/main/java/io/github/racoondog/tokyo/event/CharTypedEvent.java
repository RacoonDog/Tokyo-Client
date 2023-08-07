package io.github.racoondog.tokyo.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CharTypedEvent {
    private static final CharTypedEvent INSTANCE = new CharTypedEvent();

    public int codePoint;
    public int modifiers;

    public static CharTypedEvent get(int codePoint, int modifiers) {
        INSTANCE.codePoint = codePoint;
        INSTANCE.modifiers = modifiers;
        return INSTANCE;
    }
}
