package io.github.racoondog.tokyo.utils;

import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class CustomTitleModule extends Module {
    public final String modifiedTitle;

    public CustomTitleModule(Category category, String name, String title, String description) {
        super(category, name, description);
        this.modifiedTitle = title;
    }
}
