package io.github.racoondog.tokyo.mixin;

import io.github.racoondog.tokyo.systems.modules.ChatEmojis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientCommandSource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Environment(EnvType.CLIENT)
@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Shadow @Final TextFieldWidget textField;

    @Shadow
    private static int getStartOfCurrentWord(String input) {
        throw new AssertionError();
    }

    @Shadow
    public abstract void show(boolean narrateFirstSuggestion);

    @Unique private boolean shouldShow;

    @Inject(
        method = "refresh()V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;pendingSuggestions:Ljava/util/concurrent/CompletableFuture;",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER,
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;getStartOfCurrentWord(Ljava/lang/String;)I"
            )
        )
    )
    private void afterGetChatSuggestions(CallbackInfo ci) {
        if (shouldShow) show(true);
    }

    @Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientCommandSource;getChatSuggestions()Ljava/util/Collection;"))
    private Collection<String> yeah(ClientCommandSource instance) {
        if (!ChatEmojis.INSTANCE.isActive()) return instance.getChatSuggestions();

        Set<String> suggestions = new HashSet<>(instance.getChatSuggestions());

        String input = textField.getText();
        int cursorPos = textField.getCursor();

        String beforeCursor = input.substring(0, cursorPos);
        int currentWordStart = getStartOfCurrentWord(beforeCursor);

        String currentWord = beforeCursor.substring(currentWordStart);

        if (currentWord.startsWith(":")) {
            List<String> emojiSuggestions = new ArrayList<>();

            for (var emojiName : ChatEmojis.getEmojiNames()) {
                if (emojiName.startsWith(currentWord)) emojiSuggestions.add(emojiName);
            }

            if (!emojiSuggestions.isEmpty()) {
                shouldShow = true;
                suggestions.addAll(emojiSuggestions);
            } else shouldShow = false;
        } else shouldShow = false;

        return suggestions;
    }
}
