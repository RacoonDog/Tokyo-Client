package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChatEmojis extends Module {
    public static final ChatEmojis INSTANCE = new ChatEmojis();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    public final Setting<Boolean> incoming = sgGeneral.add(new BoolSetting.Builder()
        .name("incoming")
        .description("Enable chat emojis on incoming messages.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> outgoing = sgGeneral.add(new BoolSetting.Builder()
        .name("outgoing")
        .description("Enable chat emojis on outgoing messages.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> meteorEmojis = sgGeneral.add(new BoolSetting.Builder()
        .name("meteor-emojis")
        .description("Enables the Meteor Client emoji set.")
        .defaultValue(true)
        .build()
    );

    private ChatEmojis() {
        super(Tokyo.CATEGORY, "chat-emojis", "");
    }
}
