package io.github.racoondog.tokyo.utils;

import io.github.racoondog.meteorsharedaddonutils.mixin.mixin.IMicrosoftAccount;
import io.github.racoondog.tokyo.LaunchHandler;
import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.types.MicrosoftAccount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.launch.knot.KnotClient;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class InstanceBuilder {
    private final ProcessBuilder processBuilder = new ProcessBuilder();
    private final List<String> command = processBuilder.command();
    private final @Nullable Account<?> account;

    public boolean verbose = true;

    public String jre = Path.of(System.getProperty("java.home"), "bin", "javaw.exe").toString();
    public List<String> jvmOpts = new ArrayList<>(LaunchHandler.JVM_OPTS);
    public List<String> launchArgs = new ArrayList<>(List.of(LaunchHandler.LAUNCH_ARGS));

    public InstanceBuilder(@Nullable Account<?> account) {
        this.account = account;
    }

    public boolean hasArg(String name) {
        return launchArgs.contains(name);
    }

    public InstanceBuilder addArg(String arg) {
        launchArgs.add(arg);
        return this;
    }

    public InstanceBuilder modifyArg(String name, String value) {
        ArgsUtils.modifyArg(launchArgs, name, value);
        return this;
    }

    @Nullable
    public Process start() {
        processBuilder.inheritIO(); //Redirect stdout
        processBuilder.environment(); //Copy env vars

        command.add(jre);

        //Append JVM Options
        command.addAll(jvmOpts);

        //Append native libraries onto the classpath
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));

        //Set main class
        command.add(FabricLoader.getInstance().isDevelopmentEnvironment() ? "net.fabricmc.devlaunchinjector.Main" : KnotClient.class.getName());

        //Add account auth info to launch arguments
        if (account != null) {
            ArgsUtils.modifyArg(launchArgs, "--username", account.getUsername());
            if (account.getCache().uuid != null) ArgsUtils.modifyArg(launchArgs, "--uuid", account.getCache().uuid);
            if (account instanceof MicrosoftAccount msacc) ArgsUtils.modifyArg(launchArgs, "--accessToken", ((IMicrosoftAccount) msacc).invokeAuth());
        }

        //Append launch arguments
        command.addAll(launchArgs);

        try {
            if (verbose) {
                Tokyo.LOG.info("Starting new instance...");
                Tokyo.LOG.info("JRE/JDK: " + jre);
                Tokyo.LOG.info("JVM Options: " + String.join(" ", jvmOpts));
            }

            Process process = processBuilder.start();

            if (verbose) Tokyo.LOG.info("Instance started with PID {}.", process.pid());

            return process;
        } catch (IOException e) {
            if (verbose) e.printStackTrace();
            Tokyo.LOG.warn("Could not start instance...");
            return null;
        }
    }
}
