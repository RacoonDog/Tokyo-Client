package io.github.racoondog.tokyo.utils.prefix;

import io.github.racoondog.tokyo.systems.modules.Prefix;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class RainbowColor implements ISerializable<RainbowColor> {
    private double hue = 0.0f;
    public double speed = 0.05f;
    public double saturation = 0.25f;
    public double brightness = 1.0f;
    public double verticalOffset = 0.05f;
    public double horizontalOffset = 0.05f;

    public RainbowColor() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        hue += speed;
        if (hue > 1.0f) hue -= 1.0f;
    }

    public int getColor() {
        return getColor(0);
    }

    public int getColor(int offset) {
        double h = hue + horizontalOffset * offset + verticalOffset * Prefix.indexOffset;
        if (h > 1.0d) h -= 1.0d;
        return Color.HSBtoRGB((float) h, (float) saturation, (float) brightness);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound compound = new NbtCompound();
        compound.putDouble("speed", speed);
        compound.putDouble("saturation", saturation);
        compound.putDouble("brightness", brightness);
        compound.putDouble("verticalOffset", verticalOffset);
        compound.putDouble("horizontalOffset", horizontalOffset);

        return compound;
    }

    @Override
    public RainbowColor fromTag(NbtCompound tag) {
        this.speed = tag.getDouble("speed");
        this.saturation = tag.getDouble("saturation");
        this.brightness = tag.getDouble("brightness");
        this.verticalOffset = tag.getDouble("verticalOffset");
        this.horizontalOffset = tag.getDouble("horizontalOffset");
        return this;
    }

    @Override
    public String toString() {
        return "%s[speed=%s,sat=%s,bright=%s,horOff=%s,verOff=%s]"
            .formatted(hue, speed, saturation, brightness, horizontalOffset, verticalOffset);
    }
}
