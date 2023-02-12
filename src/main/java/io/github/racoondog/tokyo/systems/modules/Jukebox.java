package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Language;

import java.util.List;

@Environment(EnvType.CLIENT)
public class Jukebox extends Module {
    public static final Jukebox INSTANCE = new Jukebox();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Enum<Mode>> mode = sgGeneral.add(new EnumSetting.Builder<Enum<Mode>>()
        .name("mode")
        .defaultValue(Mode.Stop)
        .build()
    );

    private final Setting<List<SoundEvent>> list = sgGeneral.add(new SoundEventListSetting.Builder()
        .name("list")
        .defaultValue(SoundEvents.AMBIENT_CAVE.value())
        .visible(() -> !mode.get().equals(Mode.Stop))
        .build()
    );

    private final Setting<Boolean> repeat = sgGeneral.add(new BoolSetting.Builder()
        .name("repeat")
        .description("Makes music started via this module repeating.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> volume = sgGeneral.add(new DoubleSetting.Builder()
        .name("volume")
        .description("The volume of music and sound effects played from within this module.")
        .defaultValue(1.0d)
        .min(0.0d)
        .sliderRange(0.0d, 2.0d)
        .decimalPlaces(1)
        .build()
    );

    private Jukebox() {
        super(Tokyo.CATEGORY, "jukebox", "Customize in-game sound & music.");
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WSection section = list.add(theme.section("Catalog", false)).expandX().widget();

        WTable table = section.add(theme.table()).expandX().widget();

        WButton button = table.add(theme.button("Stop Music")).expandX().widget();
        button.action = this::stop;
        WButton button2 = table.add(theme.button("Stop All")).expandX().widget();
        button2.action = () -> {
            mc.getMusicTracker().stop();
            mc.getSoundManager().stopAll();
        };
        table.row();

        int index = 0;

        for (var disc : Registries.ITEM.stream().filter(MusicDiscItem.class::isInstance).map(MusicDiscItem.class::cast).toList()) {
            button(table, theme, Language.getInstance().get(disc.getTranslationKey() + ".desc").replace('Å', 'A'), disc.getSound()); //goofy ahh font issue
            index++;
            if (index % 2 == 0) table.row();
        }

        return list;
    }

    private void button(WContainer container, GuiTheme theme, String name, SoundEvent event) {
        WButton button = container.add(theme.button(name)).expandX().widget();
        button.action = () -> playSong(event);
    }

    private void stop() {
        mc.getMusicTracker().stop();
        mc.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        mc.getSoundManager().stopSounds(null, SoundCategory.RECORDS);
        mc.getSoundManager().stopSounds(null, SoundCategory.AMBIENT);
    }

    private void playSong(SoundEvent event) {
        stop();
        mc.getSoundManager().play(createInstance(event));
    }

    private PositionedSoundInstance createInstance(SoundEvent event) {
        return new PositionedSoundInstance(event.getId(), SoundCategory.RECORDS, volume.get().floatValue(), 1.0f, SoundInstance.createRandom(), repeat.get(), 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public boolean shouldCancelMusic(SoundEvent event) {
        return isActive() && (mode.get().equals(Mode.Stop) || (mode.get().equals(Mode.Blacklist) && list.get().contains(event)));
    }

    public enum Mode {
        Stop,
        Blacklist
    }
}
