package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModuleExample extends Module {
    public ModuleExample() {
        super(AddonTemplate.CATEGORY, "example", "An example module in a custom category.");
    }
}
