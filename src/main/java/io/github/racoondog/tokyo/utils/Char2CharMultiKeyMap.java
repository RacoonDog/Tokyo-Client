package io.github.racoondog.tokyo.utils;

import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Char2CharMultiKeyMap {
    private final Char2IntOpenHashMap keyResolver = new Char2IntOpenHashMap();
    private final Int2CharOpenHashMap valueResolver = new Int2CharOpenHashMap();

    public void put(char key, char value) {
        int hashedValue = Character.hashCode(value);
        keyResolver.put(key, hashedValue);
        valueResolver.put(hashedValue, value);
    }

    public char get(char key) {
        return valueResolver.get(keyResolver.get(key));
    }

    public boolean containsKey(char key) {
        return keyResolver.containsKey(key);
    }

    public char getOrDefault(char key, char defaultValue) {
        return containsKey(key) ? get(key) : defaultValue;
    }
}
