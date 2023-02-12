package io.github.racoondog.tokyo.mixin.prefix;

import io.github.racoondog.tokyo.systems.modules.Prefix;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatUtils.class, remap = false)
public abstract class ChatUtilsMixin {
    @Shadow private static Text PREFIX;
    @Shadow private static Text getPrefix() {throw new AssertionError();}

    @Shadow
    private static String forcedPrefixClassName;

    @Redirect(method = "sendMsg(ILjava/lang/String;Lnet/minecraft/util/Formatting;Lnet/minecraft/text/Text;)V", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/utils/player/ChatUtils;getPrefix()Lnet/minecraft/text/Text;"))
    private static Text modifyPrefix() {
        Text prefix = getPrefix();
        return Prefix.INSTANCE.isActive() && prefix == PREFIX ? Prefix.getMeteor() : prefix;
    }

    @Inject(method = "getPrefix", at = @At("HEAD"), cancellable = true)
    private static void injectOverrides(CallbackInfoReturnable<Text> cir) {
        if (!Prefix.INSTANCE.isActive()) return;

        String className = null;
        if (forcedPrefixClassName != null) {
            className = forcedPrefixClassName;
        } else {
            boolean foundChatUtils = false;

            for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                if (foundChatUtils) {
                    if (!element.getClassName().equals(ChatUtils.class.getName())) {
                        className = element.getClassName();
                        break;
                    }
                } else {
                    if (element.getClassName().equals(ChatUtils.class.getName())) foundChatUtils = true;
                }
            }
        }

        if (className != null) {
            for (var pair : Prefix.PREFIX_OVERRIDES) {
                if (className.startsWith(pair.getLeft())) {
                    if (forcedPrefixClassName != null) forcedPrefixClassName = null;
                    cir.setReturnValue(pair.getRight());
                    cir.cancel();
                }
            }
        }
    }
}
