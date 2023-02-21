package io.github.racoondog.tokyo.mixin;

import io.github.racoondog.tokyo.utils.CustomTitleModule;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(Module.class)
public abstract class ModuleMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/utils/Utils;nameToTitle(Ljava/lang/String;)Ljava/lang/String;"))
    private String modifyTitle(String name) {
        Module self = (Module) (Object) this;
        if (self instanceof CustomTitleModule customTitleModule) {
            return customTitleModule.modifiedTitle;
        }
        return Utils.nameToTitle(name);
    }
}
