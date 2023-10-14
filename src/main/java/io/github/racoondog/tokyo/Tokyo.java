package io.github.racoondog.tokyo;

import com.mojang.logging.LogUtils;
import io.github.racoondog.tokyo.commands.TokyoCommands;
import io.github.racoondog.tokyo.systems.TokyoSystems;
import io.github.racoondog.tokyo.systems.modules.Prefix;
import io.github.racoondog.tokyo.utils.TextUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class Tokyo extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Tokyo");
    public static final String MOD_ID = "tokyo-client";
    public static final ModContainer CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new IllegalStateException("Tokyo mod container not found!"));
    public static Tokyo INSTANCE;

    private final MutableText defaultPrefix;

    public Tokyo() {
        INSTANCE = this;

        defaultPrefix = Text.literal("")
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[");

        Color color = new Color(204, 48, 255);
        if (CONTAINER.getMetadata().containsCustomValue(MeteorClient.MOD_ID + ":color")) color.parse(CONTAINER.getMetadata().getCustomValue(MeteorClient.MOD_ID + ":color").getAsString());

        Text FADE = TextUtils.colorFade("Tokyo Client", defaultPrefix.getStyle().withBold(true), color.getPacked(), SettingColor.fromRGBA(112, 100, 129, 255), false);

        defaultPrefix.append(FADE)
            .append("] ");
    }

    //todo mod integrations? (ClientCommands, SeedMapper, nodus dev stuff, etc.)
    //todo ai chat things? idfk
    //todo starscript fuckery things like reflection and just more uses
    //todo uuid logger
    //todo beacon range viewer?
    //todo auto piglin bargain?
    //todo better notifications addon (SIM gave me permission)
    //todo auto unfriend when a certain damage threshold has been dealt
    //todo sync clients using C2C packets
    //cope?
    @Override
    public void onInitialize() {
        long startTime = System.currentTimeMillis();

        // ChatUtils prefix
        ChatUtils.registerCustomPrefix(getPackage(), Prefix::getTokyo);

        TokyoStarscript.init();
        TokyoCommands.init();
        TokyoSystems.init();

        DiscordPresence.registerCustomState("com.wildfire.gui.screen", "Changing options");

        LOG.info("Tokyo loaded in {} milliseconds.", System.currentTimeMillis() - startTime);
    }

    public static Text getDefaultPrefix() {
        return INSTANCE.defaultPrefix;
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "io.github.racoondog.tokyo";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("RacoonDog", "Tokyo-Client");
    }

    @Override
    public String getWebsite() {
        return "https://discord.gg/4RBmBCFSTc";
    }
}
