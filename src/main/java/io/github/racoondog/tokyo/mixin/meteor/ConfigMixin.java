package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.mixininterface.IConfig;
import io.github.racoondog.tokyo.utils.MinecraftFont;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
@Mixin(value = Config.class, remap = false)
public abstract class ConfigMixin implements IConfig {
    @Shadow @Final private SettingGroup sgVisual;
    @Shadow @Final public Setting<Boolean> customFont;

    @Unique private Setting<Boolean> shadows;
    @Unique private Setting<MinecraftFont> minecraftFont;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/settings/SettingGroup;add(Lmeteordevelopment/meteorclient/settings/Setting;)Lmeteordevelopment/meteorclient/settings/Setting;", ordinal = 1))
    private void inject(CallbackInfo ci) {
        shadows = sgVisual.add(new BoolSetting.Builder()
            .name("shadows")
            .visible(() -> !customFont.get())
            .defaultValue(true)
            .onChanged(o -> invalidate())
            .build()
        );

        minecraftFont = sgVisual.add(new GenericSetting.Builder<MinecraftFont>()
            .name("font")
            .visible(() -> !customFont.get())
            .defaultValue(new MinecraftFont())
            .onChanged(o -> invalidate())
            .build()
        );
    }

    @Unique
    private void invalidate() {
        if (mc.currentScreen instanceof WidgetScreen w) {
            w.invalidate();
        }
    }

    @Override
    public boolean tokyo$getShadows() {
        return shadows.get();
    }

    @Override
    public MinecraftFont tokyo$getMinecraftFont() {
        return minecraftFont.get();
    }
}
