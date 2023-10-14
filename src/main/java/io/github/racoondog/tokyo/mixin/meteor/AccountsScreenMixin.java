package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.systems.accounts.AddAccessTokenAccountScreen;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.screens.accounts.AccountsScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(value = AccountsScreen.class, remap = false)
public abstract class AccountsScreenMixin extends WindowScreen {
    private AccountsScreenMixin(GuiTheme theme, WWidget icon, String title) {
        super(theme, icon, title);
    }

    @Shadow protected abstract void addButton(WContainer c, String text, Runnable action);

    @Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/screens/accounts/AccountsScreen;addButton(Lmeteordevelopment/meteorclient/gui/widgets/containers/WContainer;Ljava/lang/String;Ljava/lang/Runnable;)V", ordinal = 3))
    private void injectButton(AccountsScreen instance, WContainer c, String text, Runnable action) {
        addButton(c, text, action);
        addButton(c, "Access Token", () -> MinecraftClient.getInstance().setScreen(new AddAccessTokenAccountScreen(theme, (AccountsScreen) (Object) this)));
    }
}
