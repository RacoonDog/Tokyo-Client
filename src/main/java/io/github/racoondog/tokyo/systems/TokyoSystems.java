package io.github.racoondog.tokyo.systems;

import io.github.racoondog.tokyo.commands.TokyoCommands;
import io.github.racoondog.tokyo.mixin.meteor.ISystems;
import io.github.racoondog.tokyo.systems.config.TokyoConfig;
import io.github.racoondog.tokyo.systems.hud.ImageHud;
import io.github.racoondog.tokyo.systems.modules.*;
import io.github.racoondog.tokyo.systems.seedresolver.SeedResolver;
import io.github.racoondog.tokyo.systems.themes.DarkPurpleTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Modules;

public final class TokyoSystems {
    public static void init() {
        Modules modules = Modules.get();
        Hud hud = Hud.get();

        modules.add(ChatManager.INSTANCE);
        modules.add(AutoTpa.INSTANCE);
        modules.add(Announcer.INSTANCE);
        modules.add(Prefix.INSTANCE);
        modules.add(UwUChat.INSTANCE);
        modules.add(Jukebox.INSTANCE);
        modules.add(SpamPlus.INSTANCE);
        modules.add(DiscordSRV.INSTANCE);
        modules.add(TokyoBetterChat.INSTANCE);

        hud.register(ImageHud.INFO);

        TokyoCommands.init();

        GuiThemes.add(DarkPurpleTheme.INSTANCE);

        ISystems.tokyo$invokeAdd(TokyoConfig.INSTANCE);
        ISystems.tokyo$invokeAdd(SeedResolver.INSTANCE);

        Tabs.add(TokyoConfig.TokyoConfigTab.INSTANCE);

        // Post Load
        Systems.addPreLoadTask(ChatManager.INSTANCE::toggle);
    }
}
