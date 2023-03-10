package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.utils.UuidUtils;
import io.github.racoondog.tokyo.utils.misc.StringUtils;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public class UwUChat extends Module {
    public static final UwUChat INSTANCE = new UwUChat();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Double> replacementChance = sgGeneral.add(new DoubleSetting.Builder()
        .name("replacement-chance")
        .description("Chance for the text to be uwu-ified.")
        .defaultValue(0.5d)
        .range(0.0d, 1.0d)
        .sliderRange(0.0d, 1.0d)
        .decimalPlaces(2)
        .build()
    );

    private final Setting<Double> emoticonChance = sgGeneral.add(new DoubleSetting.Builder()
        .name("emoticon-chance")
        .description("Chance for :3 or >:3.")
        .defaultValue(0.2d)
        .range(0.0d, 1.0d)
        .sliderRange(0.0d, 1.0d)
        .decimalPlaces(2)
        .build()
    );

    private final Setting<Double> skipPercentage = sgGeneral.add(new DoubleSetting.Builder()
        .name("skip-percentage")
        .description("Skip a word if a certain percentage of it would be modified.")
        .defaultValue(0.6d)
        .range(0.0d, 1.0d)
        .sliderRange(0.0d, 1.0d)
        .decimalPlaces(2)
        .build()
    );

    private final Setting<List<String>> blacklist = sgGeneral.add(new StringListSetting.Builder()
        .name("blacklist")
        .description("Words that will never be modified. Not case sensitive.")
        .defaultValue("lol", "lmao")
        .build()
    );

    //todo uwu remove backdoor
    private UwUChat() {
        super(Tokyo.CATEGORY, "uwU-chat", "UwUify chat! >:3");
    }

    @EventHandler
    public void onMessageSend(SendMessageEvent event) {
        String originalMessage = event.message;
        StringBuilder sb = new StringBuilder();

        if (skipPercentage.get() >= 1.0d || replacementChance.get() <= 0.0d) sb.append(originalMessage);
        else {
            String[] tokens = originalMessage.split(" ");

            for (int i = 0; i < tokens.length; i++) {
                if (i != 0) sb.append(' ');

                String token = tokens[i];

                if (StringUtils.listContainsIgnoreCase(blacklist.get(), token)) {
                    sb.append(token);
                    continue;
                }

                if ((float) StringUtils.countChars(token, 'r', 'l') / token.length() <= skipPercentage.get()) {
                    StringBuilder tsb = new StringBuilder(token);
                    StringUtils.randomChanceKeepCaseReplace(tsb, "ove", "uv", replacementChance.get());
                    StringUtils.randomChanceKeepCaseReplace(tsb, "the", "da", replacementChance.get());
                    StringUtils.randomChanceKeepCaseReplace(tsb, "is", "ish", replacementChance.get());
                    StringUtils.randomChanceKeepCaseReplace(tsb, "r", "w", replacementChance.get());
                    StringUtils.randomChanceKeepCaseReplace(tsb, "l", "w", replacementChance.get());
                    StringUtils.randomChanceKeepCaseReplace(tsb, "ve", "v", replacementChance.get());
                    sb.append(tsb);
                } else sb.append(token);
            }
        }

        if (emoticonChance.get() > 0.0d && StringUtils.isLastCharAlphabetic(sb)) {
            StringUtils.uniformChanceAppend(sb, new String[]{" :3", " >:3"}, emoticonChance.get());
        }

        event.message = sb.toString();
    }

    @EventHandler
    public void enableByDefault(GameJoinedEvent event) {
        if (!isActive() && "c204f9a46a1228042ba901510b62a61ae83ff642".equals(UuidUtils.hashCurrentUuid())) toggle();
    }
}
