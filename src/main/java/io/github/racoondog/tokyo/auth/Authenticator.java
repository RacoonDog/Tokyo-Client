package io.github.racoondog.tokyo.auth;

import io.github.racoondog.tokyo.Tokyo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class Authenticator {
    private static final ScheduledExecutorService DEBUGGER_CHECK_THREAD = Executors.newScheduledThreadPool(1);
    private static String hwid = null;

    public static void init() {
        try {
            DEBUGGER_CHECK_THREAD.scheduleAtFixedRate(IntegrityChecker::debuggerCheck, 0, 500, TimeUnit.MILLISECONDS);
            //stuff
        } catch (Exception ignored) {
            fail("Failed to authenticate!");
        }
    }

    public static void fail(String reason) {
        Tokyo.LOG.warn(reason);
        MinecraftClient.getInstance().close();
    }

    public static String getHwid() {
        if (hwid != null) return hwid;
        String os = System.getProperty("os.name");
        String user = System.getProperty("user.name");
        try {
            String device = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //String pre = System.getProperty("user.name") + InetAddress.getLocalHost().getHostName();
        return user;
    }

    public static String getDeviceName() {
        try {
            String localAddress = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
