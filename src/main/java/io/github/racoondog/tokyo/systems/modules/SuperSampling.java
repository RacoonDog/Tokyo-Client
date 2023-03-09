package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.mixin.supersampling.IWindow;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
public class SuperSampling extends Module {
    public static final SuperSampling INSTANCE = new SuperSampling();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Integer> sampleSize = sgGeneral.add(new IntSetting.Builder()
        .name("sample-size")
        .description("Note: Exponential scale, real multiplier is 2^x.")
        .defaultValue(1)
        .sliderRange(-1, 2)
        .onChanged(i -> scale())
        .build()
    );

    public double sampleMultiplier = 1;
    public double sampleMultiplierSquared = 1;
    public boolean supersampled = false;
    public boolean queueBufferUpdate = false;

    private SuperSampling() {
        super(Tokyo.CATEGORY, "super-sampling", "Oversampling-based anti-aliasing, courtesy of QDAA.");
    }

    private void scale() {
        sampleMultiplier = Math.pow(2, sampleSize.get());
        sampleMultiplierSquared = sampleMultiplier * sampleMultiplier;
        queueBufferUpdate = true;
    }

    public void updateBuffer() {
        queueBufferUpdate = false;

        if (isActive() != supersampled) {
            supersampled = isActive();

            Window window = mc.getWindow();

            if (!supersampled) {
                window.setFramebufferWidth((int) (window.getFramebufferWidth() / sampleMultiplier));
                window.setFramebufferHeight((int) (window.getFramebufferHeight() / sampleMultiplier));
            } else  {
                window.setFramebufferWidth((int) (window.getFramebufferWidth() * sampleMultiplier));
                window.setFramebufferHeight((int) (window.getFramebufferHeight() * sampleMultiplier));
            }

            mc.onResolutionChanged();
            if (mc.currentScreen instanceof WidgetScreen) ((WidgetScreen) mc.currentScreen).invalidate();
        }
    }
}
