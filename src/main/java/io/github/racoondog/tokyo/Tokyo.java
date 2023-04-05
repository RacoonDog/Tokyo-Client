package io.github.racoondog.tokyo;

import com.mojang.logging.LogUtils;
import io.github.racoondog.meteorsharedaddonutils.features.TitleScreenCredits;
import io.github.racoondog.meteorsharedaddonutils.mixin.mixininterface.IMeteorAddon;
import io.github.racoondog.tokyo.systems.TokyoStarscript;
import io.github.racoondog.tokyo.systems.commands.QuickLaunchCommand;
import io.github.racoondog.tokyo.systems.hud.ImageHud;
import io.github.racoondog.tokyo.systems.modules.*;
import io.github.racoondog.tokyo.utils.TextUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
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

    private MutableText prefix;

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
        prefix = Text.literal("")
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[");

        Text FADE = TextUtils.colorFade("Tokyo Client", prefix.getStyle().withBold(true), this.color.getPacked(), SettingColor.fromRGBA(112, 100, 129, 255));

        prefix.append(FADE)
            .append("] ");

        ChatUtils.registerCustomPrefix(getPackage(), Prefix::getTokyo);

        // Title Screen Credit
        final String versionString = CONTAINER.getMetadata().getVersion().getFriendlyString();
        TitleScreenCredits.modifyAddonCredit(this, credit -> {
            credit.sections.add(1, new TitleScreenCredits.Section(" (", TitleScreenCredits.GRAY));
            credit.sections.add(2, new TitleScreenCredits.Section(versionString, TitleScreenCredits.WHITE));
            credit.sections.add(3, new TitleScreenCredits.Section(")", TitleScreenCredits.GRAY));

            credit.sections.set(0, new TitleScreenCredits.Section(FADE));
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

        // Modules
        Modules.get().add(ChatManager.INSTANCE);
        Modules.get().add(AutoTpa.INSTANCE);
        Modules.get().add(Announcer.INSTANCE);
        Modules.get().add(Prefix.INSTANCE);
        Modules.get().add(UwUChat.INSTANCE);
        Modules.get().add(Jukebox.INSTANCE);
        Modules.get().add(SpamPlus.INSTANCE);
        Modules.get().add(AutoUnfriend.INSTANCE);
        Modules.get().add(DiscordSRV.INSTANCE);
        Modules.get().add(TokyoBetterChat.INSTANCE);

        Hud.get().register(ImageHud.INFO);

        Commands.get().add(QuickLaunchCommand.INSTANCE);

        Systems.addPreLoadTask(ChatManager.INSTANCE::toggle);

        TokyoStarscript.init();

        LOG.info("Tokyo loaded in {} milliseconds.", System.currentTimeMillis() - startTime);
    }

    public static Text getDefaultPrefix() {
        return INSTANCE.prefix;
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "io.github.racoondog.tokyo";
    }
}
