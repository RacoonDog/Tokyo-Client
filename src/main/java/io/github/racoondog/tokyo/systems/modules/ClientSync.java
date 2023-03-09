package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientSync extends Module {
    public static final ClientSync INSTANCE = new ClientSync();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgReceive = this.settings.createGroup("Receive");
    private final SettingGroup sgSend = this.settings.createGroup("Send");

    private final Setting<Boolean> receive = sgGeneral.add(new BoolSetting.Builder()
        .name("receive")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> send = sgGeneral.add(new BoolSetting.Builder()
        .name("send")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> chatReceive = sgReceive.add(new BoolSetting.Builder()
        .name("chat-receive")
        .defaultValue(false)
        .visible(receive::get)
        .build()
    );

    private final Setting<Boolean> websocketReceive = sgReceive.add(new BoolSetting.Builder()
        .name("websocket-receive")
        .defaultValue(true)
        .visible(receive::get)
        .build()
    );

    private ClientSync() {
        super(Tokyo.CATEGORY, "client-sync", "");
    }
}
