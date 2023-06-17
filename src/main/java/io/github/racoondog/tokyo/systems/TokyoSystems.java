package io.github.racoondog.tokyo.systems;

import io.github.racoondog.meteorsharedaddonutils.mixin.mixin.ISystems;
import io.github.racoondog.tokyo.systems.commands.*;
import io.github.racoondog.tokyo.systems.hud.ImageHud;
import io.github.racoondog.tokyo.systems.modules.*;
import io.github.racoondog.tokyo.systems.config.TokyoConfig;
import io.github.racoondog.tokyo.systems.seedresolver.SeedResolver;
import io.github.racoondog.tokyo.systems.themes.DarkPurpleTheme;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.loader.api.FabricLoader;

public final class TokyoSystems {
    private static final Modules MODULES = Modules.get();
    private static final Hud HUD = Hud.get();

    public static void initialize() {
        MODULES.add(ChatManager.INSTANCE);
        MODULES.add(AutoTpa.INSTANCE);
        MODULES.add(Announcer.INSTANCE);
        MODULES.add(Prefix.INSTANCE);
        MODULES.add(UwUChat.INSTANCE);
        MODULES.add(Jukebox.INSTANCE);
        MODULES.add(SpamPlus.INSTANCE);
        MODULES.add(AutoUnfriend.INSTANCE);
        MODULES.add(DiscordSRV.INSTANCE);
        MODULES.add(TokyoBetterChat.INSTANCE);
        MODULES.add(ChatEmojis.INSTANCE);
        MODULES.add(Sprint.INSTANCE);
        MODULES.add(NewChunks.INSTANCE);
        MODULES.add(StrafeSpeed.INSTANCE);

        HUD.register(ImageHud.INFO);

        Commands.add(QuickLaunchCommand.INSTANCE);
        Commands.add(ShareCommand.INSTANCE);
        Commands.add(ChunkInfoCommand.INSTANCE);
        Commands.add(LookAtCommand.INSTANCE);

        GuiThemes.add(DarkPurpleTheme.INSTANCE);

        ISystems.invokeAdd(TokyoConfig.INSTANCE);
        ISystems.invokeAdd(SeedResolver.INSTANCE);

        Tabs.add(TokyoConfig.TokyoConfigTab.INSTANCE);

        TokyoStarscript.init();

        // Post Load
        Systems.addPreLoadTask(ChatManager.INSTANCE::toggle);
    }
}
