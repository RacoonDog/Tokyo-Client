package io.github.racoondog.tokyo;

import io.github.racoondog.launchargsapi.api.ArgsListener;
import io.github.racoondog.tokyo.utils.SwarmUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.PostInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LaunchHandler implements ArgsListener {
    public static final String[] LAUNCH_ARGS = FabricLoader.getInstance().getLaunchArguments(true);
    public static final List<String> JVM_OPTS = ManagementFactory.getRuntimeMXBean().getInputArguments();

    private static OptionSpec<Void> freezeSettingsSpec;
    private static OptionSpec<Void> deactivateSpec;
    private static OptionSpec<String> swarmModeSpec;
    private static OptionSpec<String> swarmIpSpec;
    private static OptionSpec<Integer> swarmPortSpec;

    public static boolean freezeSettings;
    private static boolean deactivate;
    private static String swarmMode;
    private static String swarmIp;
    private static int swarmPort;

    @Override
    public void createSpecs(OptionParser optionParser) {
        deactivateSpec = optionParser.accepts("tokyo?deactivate");
        freezeSettingsSpec = optionParser.accepts("tokyo?freezeSettings");
        swarmModeSpec = optionParser.accepts("tokyo?swarmMode").withRequiredArg();
        swarmIpSpec = optionParser.accepts("tokyo?swarmIp").withRequiredArg();
        swarmPortSpec = optionParser.accepts("tokyo?swarmPortSpec").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
    }

    @Override
    public void parseArgs(OptionSet optionSet) {
        freezeSettings = optionSet.has(freezeSettingsSpec);
        deactivate = optionSet.has(deactivateSpec);
        swarmMode = optionSet.valueOf(swarmModeSpec);
        swarmIp = optionSet.valueOf(swarmIpSpec);
        swarmPort = optionSet.valueOf(swarmPortSpec);
    }

    @PostInit
    public static void postInit() {
        //parse launch args
        if (deactivate) {
            new ArrayList<>(Modules.get().getActive()).forEach(Module::toggle);
            Hud.get().active = false;
        }

        //after deactivate
        if (swarmMode != null) {
            SwarmUtils.configPort(swarmPort);

            if (swarmMode.equals("worker")) {
                SwarmUtils.configIp(swarmIp);
                SwarmUtils.beginWorker();
            } else if (swarmMode.equals("host")) SwarmUtils.beginHost();
        }
    }
}
