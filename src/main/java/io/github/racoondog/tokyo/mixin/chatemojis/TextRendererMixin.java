package io.github.racoondog.tokyo.mixin.chatemojis;

import io.github.racoondog.tokyo.systems.modules.ChatEmojis;
import io.github.racoondog.tokyo.utils.TextUtils;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.regex.Matcher;

@Environment(EnvType.CLIENT)
@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {
    @Shadow protected abstract int draw(OrderedText text, float x, float y, int color, Matrix4f matrix4f, boolean shadow);

    @Shadow @Final public int fontHeight;

    @Inject(method = "drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I", at = @At("HEAD"), cancellable = true)
    private void injectDrawWithShadow(MatrixStack matrices, OrderedText text, float x, float y, int color, CallbackInfoReturnable<Integer> cir) {
        if (!ChatEmojis.shouldRender()) return;

        List<Pair<String, Style>> dissected = TextUtils.dissect(text);

        x = drawWithEmoji(dissected, matrices, x, y, color, true);
        cir.setReturnValue((int) x + 1);
    }

    @Unique
    private float drawWithEmoji(List<Pair<String, Style>> dissected, MatrixStack matrices, float x, float y, int color, boolean shadow) {
        for (var textEntry : dissected) {
            x = drawWithEmoji(textEntry.left(), textEntry.right(), matrices, x, y, color, shadow);
        }

        return x;
    }

    @Unique
    private float drawWithEmoji(String content, Style style, MatrixStack matrices, float x, float y, int color, boolean shadow) {
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        Matcher matcher = ChatEmojis.EMOJI_REGEX.matcher(content);

        if (matcher.find()) {
            do {
                ChatEmojis.Emoji emoji = ChatEmojis.get(matcher.group());

                if (emoji == null) {
                    if (matcher.end() == content.length())
                        return drawShadowFix(TextUtils.toOrderedText(content, style), x, y, color, positionMatrix, shadow);
                    else {
                        x = drawShadowFix(TextUtils.toOrderedText(content.substring(0, matcher.end()), style), x, y, color, positionMatrix, shadow);
                        content = content.substring(matcher.end());
                        matcher.reset(content);
                        continue;
                    }
                }

                if (matcher.start() > 0) {
                    x = drawShadowFix(TextUtils.toOrderedText(content.substring(0, matcher.start()), style), x, y, color, positionMatrix, shadow);
                }

                emoji.render(matrices, (int) x, (int) y, fontHeight);
                x += fontHeight;

                content = content.substring(matcher.end());
                matcher.reset(content);
            } while (matcher.find());
            if (content.isEmpty()) return x;
        }
        return drawShadowFix(TextUtils.toOrderedText(content, style), x, y, color, positionMatrix, shadow);
    }

    @Unique
    private int drawShadowFix(OrderedText text, float x, float y, int color, Matrix4f matrix4f, boolean shadow) {
        x = draw(text, x, y, color, matrix4f, shadow);
        return (int) x - (shadow ? 1 : 0);
    }
}
