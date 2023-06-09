package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.systems.modules.ChatManager;
import io.github.racoondog.tokyo.systems.modules.Prefix;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatUtils.class, remap = false)
public abstract class ChatUtilsMixin {
    @Shadow
    private static Text PREFIX;

    @Inject(method = "getPrefix", at = @At("RETURN"), cancellable = true)
    private static void modifyPrefix(CallbackInfoReturnable<Text> cir) {
        if (Prefix.INSTANCE.isActive() && cir.getReturnValue() == PREFIX) cir.setReturnValue(Prefix.getMeteor());
    }
    
    @Inject(method = "sendPlayerMsg", at = @At("HEAD"), cancellable = true)
    private static void queueMessage(String message, CallbackInfo ci) {
        if (ChatManager.INSTANCE.isActive()) {
            ChatManager.INSTANCE.queueSend(message, message.startsWith("/") ? ChatManager.Priority.Command : ChatManager.Priority.Meteor);
            ci.cancel();
        }
    }
}
