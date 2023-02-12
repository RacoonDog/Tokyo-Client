package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class AutoUnfriend extends Module {
    public static final AutoUnfriend INSTANCE = new AutoUnfriend();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Double> damageThreshold = sgGeneral.add(new DoubleSetting.Builder()
        .name("damage-threshold")
        .description("Damage dealt before a player is unfriended.")
        .defaultValue(8.0d)
        .min(0.0d)
        .sliderRange(0.0d, 40.0d)
        .build()
    );

    private final Setting<Double> heartThreshold = sgGeneral.add(new DoubleSetting.Builder()
        .name("heart-threshold")
        .description("Hearts lost before a player is unfriended.")
        .defaultValue(4.0d)
        .min(0.0d)
        .sliderRange(0.0d, 20.0d)
        .build()
    );

    private final Setting<Boolean> crystal = sgGeneral.add(new BoolSetting.Builder()
        .name("crystal")
        .description("Unfriend a player whenever a crystal is placed near you.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> crystalRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("crystal-range")
        .description("How close a crystal needs to be before a player is unfriended.")
        .defaultValue(4.0d)
        .min(0.0d)
        .sliderRange(0.0d, 5.0d)
        .build()
    );

    private AutoUnfriend() {
        super(Tokyo.CATEGORY, "auto-unfriend", "Automatically unfriend users under specific circumstances.");
    }

    @EventHandler
    private void onDamage(DamageEvent event) {
        if (event.entity != mc.player) return;
        Entity source = event.source.getSource();
        if (source instanceof PlayerEntity playerEntity) {
            Friend friend = Friends.get().get(playerEntity);
            if (friend == null) return;

            //todo idk make DamageEvent get the damage????

            Friends.get().remove(friend);
        }
    }
}
