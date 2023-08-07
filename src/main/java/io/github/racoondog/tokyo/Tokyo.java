package io.github.racoondog.tokyo;

import com.mojang.logging.LogUtils;
import io.github.racoondog.meteorsharedaddonutils.features.TitleScreenCredits;
import io.github.racoondog.meteorsharedaddonutils.mixin.mixininterface.IMeteorAddon;
import io.github.racoondog.tokyo.systems.TokyoSystems;
import io.github.racoondog.tokyo.systems.modules.*;
import io.github.racoondog.tokyo.utils.TextUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
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

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class Tokyo extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Tokyo");
    public static final String MOD_ID = "tokyo-client";
    public static final ModContainer CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new IllegalStateException("Tokyo mod container not found!"));
    public static Tokyo INSTANCE;

    private MutableText defaultPrefix;

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

        INSTANCE = this;

        // ChatUtils prefix
        defaultPrefix = Text.literal("")
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[");

        Text FADE = TextUtils.colorFade("Tokyo Client", defaultPrefix.getStyle().withBold(true), this.color.getPacked(), SettingColor.fromRGBA(112, 100, 129, 255));

        defaultPrefix.append(FADE)
            .append("] ");

        ChatUtils.registerCustomPrefix(getPackage(), Prefix::getTokyo);

        // Title Screen Credit
        final String versionString = CONTAINER.getMetadata().getVersion().getFriendlyString();
        TitleScreenCredits.modifyAddonCredit(this, credit -> {
            credit.sections.add(1, new TitleScreenCredits.Section(" (", TitleScreenCredits.GRAY));
            credit.sections.add(2, new TitleScreenCredits.Section(versionString, TitleScreenCredits.WHITE));
            credit.sections.add(3, new TitleScreenCredits.Section(")", TitleScreenCredits.GRAY));

            credit.sections.set(0, new TitleScreenCredits.Section(Prefix.INSTANCE.tokyoPrefixTextSetting.get().get()));
        });

        // Version strings for other addons
        TitleScreenCredits.registerGlobalCreditModification(credit -> {
            String id = credit.addon.name.equals("Meteor Client") ? "meteor-client" : ((IMeteorAddon) credit.addon).getId();
            Optional<ModContainer> optionalModContainer = FabricLoader.getInstance().getModContainer(id);
            if (optionalModContainer.isEmpty()) {
                LOG.info("Could not find mod container for {} (id: {}).", credit.addon.name, id);
                return;
            }
            String localVersionString = optionalModContainer.get().getMetadata().getVersion().getFriendlyString();

            credit.sections.add(1, new TitleScreenCredits.Section(" (", TitleScreenCredits.GRAY));
            credit.sections.add(2, new TitleScreenCredits.Section(localVersionString, TitleScreenCredits.WHITE));
            credit.sections.add(3, new TitleScreenCredits.Section(")", TitleScreenCredits.GRAY));
        });

        TokyoSystems.initialize();

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
