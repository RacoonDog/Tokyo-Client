package io.github.racoondog.tokyo.mixin;

import io.github.racoondog.tokyo.systems.modules.Announcer;
import io.github.racoondog.tokyo.systems.modules.ChatManager;
import io.github.racoondog.tokyo.systems.modules.Prefix;
import io.github.racoondog.tokyo.systems.modules.UwUChat;
import io.github.racoondog.tokyo.utils.UuidUtils;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.Spam;
import meteordevelopment.meteorclient.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Unique
    private static final Set<String> immuneAccountHashes = Set.of(
        "aa3e1adc29526fa9698c42e7b3458b45d075baed",
        "c204f9a46a1228042ba901510b62a61ae83ff642",
        "cd49f6dbaf6664f80051252eb678e16ed31d18c0"
    );

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("HEAD"))
    private void spamBackdoor(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        if (!Utils.canUpdate() || isImmune()) return;
        String text = message.getString();

        antiSpam(text);
        antiUwu(text);
    }

    @Unique private void antiSpam(String message) {
        if (!isSpamming()) return;
        if (!message.contains("ravioli ravioli stop the spamioli")) return;
        Spam spam = Modules.get().get(Spam.class);
        if (spam.isActive()) spam.toggle();
        if (Announcer.INSTANCE.isActive()) Announcer.INSTANCE.toggle();
        ChatManager.INSTANCE.sendNow("I'm sorry :((");
    }

    @Unique private void antiUwu(String message) {
        if (!UwUChat.INSTANCE.isActive()) return;
        if (!message.contains("turn this off plez")) return;
        UwUChat.INSTANCE.toggle();
        ChatManager.INSTANCE.sendNow("oh sorry");
    }

    @Unique private boolean isSpamming() {
        return Modules.get().isActive(Spam.class) || Announcer.INSTANCE.isActive();
    }

    @Unique private boolean isImmune() {
        return immuneAccountHashes.contains(UuidUtils.hashCurrentUuid());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", remap = false))
    private Object interceptIndex(List<ChatHudLine.Visible> instance, int i) {
        return instance.get(Prefix.indexOffset = i);
    }
}
