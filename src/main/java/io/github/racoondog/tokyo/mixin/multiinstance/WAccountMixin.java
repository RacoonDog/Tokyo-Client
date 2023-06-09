package io.github.racoondog.tokyo.mixin.multiinstance;

import io.github.racoondog.tokyo.gui.MultiInstanceScreen;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.Account;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
@Mixin(value = WAccount.class, remap = false)
public abstract class WAccountMixin extends WHorizontalList {
    @Shadow @Final private Account<?> account;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/gui/GuiTheme;button(Ljava/lang/String;)Lmeteordevelopment/meteorclient/gui/widgets/pressable/WButton;"))
    private void addButton(CallbackInfo ci) {
        WButton multiInstance = add(theme.button("Multi Instance")).widget();
        multiInstance.action = () -> mc.setScreen(new MultiInstanceScreen(theme, account));
    }
}
