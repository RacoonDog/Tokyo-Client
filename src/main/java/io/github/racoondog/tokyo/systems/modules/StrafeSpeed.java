package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Collections;

@Environment(EnvType.CLIENT)
public class StrafeSpeed extends Module {
    public static final StrafeSpeed INSTANCE = new StrafeSpeed();

    private StrafeSpeed() {
        super(Tokyo.CATEGORY, "strafe-speed", "");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        Rotations.rotate(0, 0, () -> {

        });
    }
}
