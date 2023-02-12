package io.github.racoondog.tokyo.mixin.chatmanager;

import io.github.racoondog.tokyo.systems.modules.ChatManager;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatUtils.class, remap = false)
public abstract class ChatUtilsMixin {
    @Inject(method = "sendPlayerMsg", at = @At("HEAD"), cancellable = true)
    private static void queueMessage(String message, CallbackInfo ci) {
        if (ChatManager.INSTANCE.isActive()) {
            ChatManager.INSTANCE.queueSend(message, message.startsWith("/") ? ChatManager.Priority.Command : ChatManager.Priority.Meteor);
            ci.cancel();
        }
    }
}
