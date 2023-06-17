package io.github.racoondog.tokyo.systems.config;

import io.github.racoondog.tokyo.systems.seedresolver.SeedResolver;
import io.github.racoondog.tokyo.utils.settings.OrderedEnumSetting;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class TokyoConfig extends System<TokyoConfig> {
    public static final TokyoConfig INSTANCE = new TokyoConfig();
    public final Settings settings = new Settings();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgC2C = this.settings.createGroup("Client Sync");

    // General

    public final Setting<Boolean> screenOverlays = sgGeneral.add(new BoolSetting.Builder()
        .name("screen-overlays")
        .description("Enable screen overlays.")
        .defaultValue(true)
        .build()
    );

    public final Setting<List<SeedResolver.ResolutionMethod>> seedResolutionMethods = sgGeneral.add(new OrderedEnumSetting.Builder<SeedResolver.ResolutionMethod>()
        .name("seed-resolution-methods")
        .description("Priority")
        .defaultValue(SeedResolver.ResolutionMethod.CommandSource, SeedResolver.ResolutionMethod.SeedConfig, SeedResolver.ResolutionMethod.SavedSeedConfigs, SeedResolver.ResolutionMethod.OnlineDatabase)
        .build()
    );

    // C2C

    public final Setting<Boolean> c2cWebsocket = sgC2C.add(new BoolSetting.Builder()
        .name("websocket")
        .description("")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> c2cChat = sgC2C.add(new BoolSetting.Builder()
        .name("chat")
        .description("")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> c2cWhisper = sgC2C.add(new BoolSetting.Builder()
        .name("whisper")
        .description("")
        .defaultValue(true)
        .build()
    );

    public final Setting<String> packetIdentifier = sgC2C.add(new StringSetting.Builder()
        .name("packet-identifier")
        .description("")
        .defaultValue("C2C:")
        .visible(() -> c2cChat.get() || c2cWhisper.get() || c2cWebsocket.get())
        .build()
    );

    public final Setting<String> encryptionKey = sgC2C.add(new StringSetting.Builder()
        .name("encryption-key")
        .description("")
        .defaultValue(UUID.randomUUID().toString())
        .visible(c2cChat::get)
        .build()
    );

    private TokyoConfig() {
        super("tokyo-config");

        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public TokyoConfig fromTag(NbtCompound tag) {
        if (tag.contains("settings")) settings.fromTag(tag.getCompound("settings"));

        return this;
    }

    public static class TokyoConfigTab extends Tab {
        public static final TokyoConfigTab INSTANCE = new TokyoConfigTab();

        private TokyoConfigTab() {
            super("Tokyo");
        }

        @Override
        public TabScreen createScreen(GuiTheme theme) {
            return new TokyoConfigScreen(theme);
        }

        @Override
        public boolean isScreen(Screen screen) {
            return screen instanceof TokyoConfigScreen;
        }
    }

    public static class TokyoConfigScreen extends WindowTabScreen {
        private final Settings settings = TokyoConfig.INSTANCE.settings;

        public TokyoConfigScreen(GuiTheme theme) {
            super(theme, TokyoConfigTab.INSTANCE);

            settings.onActivated();
        }

        @Override
        public void initWidgets() {
            add(theme.settings(settings)).expandX();
        }

        @Override
        public void tick() {
            super.tick();

            settings.tick(window, theme);
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(TokyoConfig.INSTANCE);
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(TokyoConfig.INSTANCE);
        }
    }
}
