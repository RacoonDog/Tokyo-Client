package io.github.racoondog.tokyo.systems.modules;

import com.google.common.collect.Lists;
import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class AutoTpa extends Module {
    public static final AutoTpa INSTANCE = new AutoTpa();
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Filter> filter = sgGeneral.add(new EnumSetting.Builder<Filter>()
        .name("filter")
        .defaultValue(Filter.Friends)
        .build()
    );

    private final Setting<List<String>> filterList = sgGeneral.add(new StringListSetting.Builder()
        .name("filter-list")
        .defaultValue(Lists.newArrayList("McChiggenLord", "RickyTheRacc"))
        .visible(() -> filter.get().isList)
        .build()
    );

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.Accept)
        .build()
    );

    private final Setting<String> acceptCommand = sgGeneral.add(new StringSetting.Builder()
        .name("accept-command")
        .description("Tpa accept command with '%s' in place of the sender's username.")
        .defaultValue("/tpaccept %s")
        .visible(() -> mode.get().equals(Mode.Accept))
        .build()
    );

    private final Setting<String> denyCommand = sgGeneral.add(new StringSetting.Builder()
        .name("deny-command")
        .description("Tpa deny command with '%s' in place of the sender's username.")
        .defaultValue("/tpdeny %s")
        .visible(() -> mode.get().equals(Mode.Deny))
        .build()
    );

    private final Setting<String> detect = sgGeneral.add(new StringSetting.Builder()
        .name("detect")
        .description("Regex used to detect a TPA request, with \"username\" as the named capture group to find the username.")
        .defaultValue("(^|\\s)(?<username>[a-zA-Z0-9_.]*?) has requested to teleport to you\\.")
        .onChanged(o -> recompileRegex())
        .wide()
        .build()
    );

    private Pattern detectPattern;

    private AutoTpa() {
        super(Tokyo.CATEGORY, "auto-tpa", "Automatically accept or deny TPA requests.");
        recompileRegex();
    }

    private void recompileRegex() {
        detectPattern = Pattern.compile(detect.get());
    }

    @EventHandler
    private void onMessageReceived(ReceiveMessageEvent event) {
        if (!Utils.canUpdate() || event.isModified() || event.isCancelled()) return;

        String message = event.getMessage().getString();
        Matcher matcher = detectPattern.matcher(message);

        if (!matcher.find()) return;
        String username = matcher.group("username");

        switch (filter.get()) {
            case All -> send(username);
            case Friends -> {
                if (Friends.get().get(username) != null) send(username);
            }
            case Blacklist -> {
                if (!filterList.get().contains(username)) send(username);
            }
            case Whitelist -> {
                if (filterList.get().contains(username)) send(username);
            }
        }
    }

    private void send(String name) {
        ChatUtils.sendPlayerMsg(switch (mode.get()) {
                case Accept -> acceptCommand.get().formatted(name);
                case Deny -> denyCommand.get().formatted(name);
        });
    }

    public enum Filter {
        All(false),
        Friends(false),
        Blacklist(true),
        Whitelist(true);

        private final boolean isList;

        Filter(boolean isList) {
            this.isList = isList;
        }
    }

    public enum Mode {
        Accept,
        Deny
    }
}
