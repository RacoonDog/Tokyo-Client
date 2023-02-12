package io.github.racoondog.tokyo.mixin.chatmanager;

import io.github.racoondog.tokyo.systems.modules.ChatManager;
import io.github.racoondog.tokyo.utils.VerboseUtils;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.Spam;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Environment(EnvType.CLIENT)
@Mixin(value = Spam.class, remap = false)
public abstract class SpamMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/settings/IntSetting$Builder;build()Lmeteordevelopment/meteorclient/settings/IntSetting;"), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=delay")))
    private IntSetting checkOnChange(IntSetting.Builder instance) {
        instance.onChanged(spamDelay -> {
            if (ChatManager.INSTANCE.isActive()) {
                if (spamDelay < ChatManager.INSTANCE.chatDelay.get()) {
                    VerboseUtils.warnInLoop("Spam delay is lower than ChatManager's chat delay!");
                    Spam spam = Modules.get().get(Spam.class);
                    if (spam.isActive()) {
                        ChatUtils.info("Tokyo", "Deactivating Spam...");
                        spam.toggle();
                    }
                }
            }
        });
        return instance.build();
    }
}
