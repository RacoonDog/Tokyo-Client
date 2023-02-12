package io.github.racoondog.tokyo.mixin.chatmanager;

import io.github.racoondog.tokyo.systems.modules.ChatManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Inject(method = "sendMessage", at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void queueMessage(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        if (ChatManager.INSTANCE.isActive()) {
            ChatManager.INSTANCE.queueSend(chatText, chatText.startsWith("/") ? ChatManager.Priority.Command : ChatManager.Priority.Chat);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
