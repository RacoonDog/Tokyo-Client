package io.github.racoondog.tokyo.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Sprint extends Module {
    public static final Sprint INSTANCE = new Sprint();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Boolean> whenStationary = sgGeneral.add(new BoolSetting.Builder()
        .name("when-stationary")
        .description("Continues sprinting even if you do not move.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> omniDirectional = sgGeneral.add(new BoolSetting.Builder()
        .name("omni-directional")
        .description("Allows you to sprint sideways and backwards.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> bypassCooldown = sgGeneral.add(new BoolSetting.Builder()
        .name("bypass-cooldown")
        .description("Bypasses the cooldown before sprinting.")
        .defaultValue(false)
        .build()
    );

    private boolean queueSprint = false;

    private Sprint() {
        super(Categories.Movement, "sprint", "Customize sprint conditions.");
    }

    @Override
    public void onActivate() {
        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        mc.player.setSprinting(mc.options.sprintKey.isPressed());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        mc.player.setSneaking(true);
        return;

        /*
        if (mc.player.isSubmergedInWater() || (mc.player.isSneaking() && !queueSprint)) return;

        System.out.println(1);

        if (!whenStationary.get() && !isMoving()) {
            if (queueSprint) mc.player.setSneaking(false);
            System.out.println("sopt[]sk");
            return;
        }

        System.out.println(2);

        if (bypassCooldown.get()) {
            if (queueSprint) {
                queueSprint = false;
                mc.player.setSneaking(false);
                mc.player.setSprinting(true);
            } else if (!mc.player.isSprinting()) {
                queueSprint = true;
                mc.player.setSneaking(true);
            }
        } else {
            mc.player.setSneaking(true);
        }
         */
    }

    private boolean isMoving() {
        return omniDirectional.get() ? mc.player.horizontalSpeed > 0 : mc.player.forwardSpeed > 0;
    }
}
