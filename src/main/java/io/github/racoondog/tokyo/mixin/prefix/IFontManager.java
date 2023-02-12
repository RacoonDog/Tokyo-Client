package io.github.racoondog.tokyo.mixin.prefix;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(FontManager.class)
public interface IFontManager {
    @Accessor("fontStorages") Map<Identifier, FontStorage> tokyo$getFontStorages();
}
