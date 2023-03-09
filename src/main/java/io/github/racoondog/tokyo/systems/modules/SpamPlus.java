package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.utils.misc.FileUtils;
import io.github.racoondog.tokyo.utils.TextPresets;
import io.github.racoondog.tokyo.utils.misc.VerboseUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SpamPlus extends Module {
    public static final SpamPlus INSTANCE = new SpamPlus();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.File)
        .onChanged(o -> reset())
        .build()
    );

    private final Setting<String> file = sgGeneral.add(new StringSetting.Builder()
        .name("file-name")
        .defaultValue("spam-plus.txt")
        .onChanged(o -> reset())
        .visible(() -> mode.get() == Mode.File)
        .build()
    );

    private final Setting<Preset> preset = sgGeneral.add(new EnumSetting.Builder<Preset>()
        .name("preset")
        .defaultValue(Preset.BeeMovie)
        .onChanged(o -> reset())
        .visible(() -> mode.get() == Mode.Preset)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay between specified messages in ticks.")
        .defaultValue(20)
        .min(0)
        .sliderMax(200)
        .onChanged(o -> {
            timer = o;
            if (ChatManager.INSTANCE.isActive()) {
                if (o < ChatManager.INSTANCE.chatDelay.get()) {
                    VerboseUtils.warnInLoop("Miminum SpamPlus delay is lower than ChatManager's chat delay!");
                    if (isActive()) {
                        info("Deactivating SpamPlus...");
                        toggle();
                    }
                }
            }
        })
        .build()
    );

    private int messageI, timer;
    private List<String> lines;


    //todo refresh from file button
    //todo spam backdoor
    private SpamPlus() {
        super(Tokyo.CATEGORY, "spam-plus", "Even better than spam.");
    }

    @Override
    public void onActivate() {
        reset();
    }

    private void reset() {
        timer = delay.get();
        messageI = 0;

        lines = switch (mode.get()) {
            case File -> FileUtils.getLines(file.get());
            case Preset -> preset.get().lines;
        };
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (lines.isEmpty()) {
            reset();
            if (lines.isEmpty()) {
                if (mode.get() == Mode.File) warning("Empty spam file!");
                else warning("Empty spam lines!");

                info("Deactivating SpamPlus...");
                toggle();
            }
        }

        if (timer-- <= 0) {
            ChatManager.INSTANCE.queueSend(lines.get(messageI++), ChatManager.Priority.Meteor);

            timer = delay.get();
            if (messageI >= lines.size()) messageI = 0;
        }
    }

    @PostInit
    public static void createFileIfMissing() {
        Path spamFile = FileUtils.getTokyoFile(INSTANCE.file.get());
        FileUtils.ensureDirectoryExists(spamFile);
        if (!Files.isRegularFile(spamFile)) {
            try {
                Files.createFile(spamFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum Mode {
        Preset,
        File
    }

    public enum Preset {
        BeeMovie(TextPresets.BEE_MOVIE);

        private final List<String> lines;

        Preset(List<String> lines) {
            this.lines = lines;
        }
    }
}
