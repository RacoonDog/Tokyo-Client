package io.github.racoondog.tokyo.utils;

import net.minecraft.text.ClickEvent;

public class RunnableClickEvent extends ClickEvent {
    public final Runnable runnable;

    public RunnableClickEvent(Runnable runnable) {
        super(null, null);
        this.runnable = runnable;
    }
}
