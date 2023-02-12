package io.github.racoondog.tokyo.auth;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class IntegrityChecker {
    public static final List<String> debuggers = ImmutableList.of("wireshark", "recaf", "dump");
    public static final int size = 3;

    public static boolean debuggerCheck() {
        return size != debuggers.size() || ProcessHandle.allProcesses().parallel().filter(ProcessHandle::isAlive).anyMatch(IntegrityChecker::doCheck);
    }

    private static boolean doCheck(ProcessHandle processHandle) {
        boolean check = getCommand(processHandle).map(IntegrityChecker::containsDebugger).orElse(false);
        if (check && !processHandle.destroy() && !processHandle.destroyForcibly()) {
            //Handle failed destruction of debugger

            throw new ConcurrentModificationException(); //"Fake" error to throw off
            //todo escalate exception to handle on main thread
        }
        return check;
    }

    private static Optional<String> getCommand(ProcessHandle processHandle) {
        ProcessHandle.Info info = processHandle.info();
        return info.command().or(info::commandLine);
    }

    private static boolean containsDebugger(String command) {
        for (var debuggerName : debuggers) if (command.contains(debuggerName)) return true;
        return false;
    }
}
