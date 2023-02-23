package io.github.racoondog.tokyo.utils;

import io.github.racoondog.meteorsharedaddonutils.mixin.mixin.ISwarm;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.Swarm;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.SwarmHost;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.SwarmWorker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class SwarmUtils {
    public static void configIp(String ip) {
        Swarm swarm = Modules.get().get(Swarm.class);
        if (swarm.isActive()) return; //Do not override
        ((ISwarm) swarm).getIpAddress().set(ip);
    }

    public static void configPort(int port) {
        Swarm swarm = Modules.get().get(Swarm.class);
        if (swarm.isActive()) return; //Do not override
        ((ISwarm) swarm).getServerPort().set(port);
    }

    public static void beginHost() {
        Swarm swarm = Modules.get().get(Swarm.class);
        if (swarm.isActive()) return; //Do not override
        swarm.mode.set(Swarm.Mode.Host);
        swarm.close();
        swarm.host = new SwarmHost(((ISwarm) swarm).getServerPort().get());
    }

    public static void beginWorker() {
        Swarm swarm = Modules.get().get(Swarm.class);
        if (swarm.isActive()) return; //Do not override
        swarm.mode.set(Swarm.Mode.Worker);
        swarm.close();
        swarm.worker = new SwarmWorker(((ISwarm) swarm).getIpAddress().get(), ((ISwarm) swarm).getServerPort().get());
    }
}
