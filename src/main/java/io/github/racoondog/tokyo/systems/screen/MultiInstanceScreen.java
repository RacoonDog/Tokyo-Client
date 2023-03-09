package io.github.racoondog.tokyo.systems.screen;

import io.github.racoondog.meteorsharedaddonutils.mixin.mixin.ISwarm;
import io.github.racoondog.tokyo.utils.ArgsUtils;
import io.github.racoondog.tokyo.utils.InstanceBuilder;
import io.github.racoondog.tokyo.utils.TableBuilder;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.screens.AccountsScreen;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.Swarm;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MultiInstanceScreen extends WindowScreen {
    private static final CharFilter NUMBER_FILTER = (text, c) -> '0' <= c && c <= '9';
    private static final CharFilter MEMORY_FILTER = (text, c) -> ('0' <= c && c <= '9') || c == 'k' || c == 'K' || c == 'm' || c == 'M' || c == 'g' || c == 'G';

    private final Account<?> account;
    private final InstanceBuilder builder;

    private WTextBox jre;
    private WTextBox xms;
    private WTextBox xmx;
    private WTextBox xmn;
    private WTextBox jvmOpts;
    private WTextBox launchArgs;
    private WTextBox width;
    private WTextBox height;
    private WDropdown<SwarmMode> swarmMode;
    private WTextBox swarmIp;
    private WTextBox swarmPort;
    private WCheckbox deactivate;

    private WButton launch;
    private WLabel info;

    public MultiInstanceScreen(GuiTheme theme, Account<?> account) {
        super(theme, "Multi Instance");

        this.account = account;
        this.builder = new InstanceBuilder(account);
    }

    @Override
    public void initWidgets() {
        TableBuilder tb = new TableBuilder(theme);

        tb.addSegment(t -> {
            t.add(theme.label("Logged in as: "));
            t.add(theme.label(account.getUsername()).color(Color.GRAY));
            t.row();
            t.add(theme.texture(48, 48, account.getCache().getHeadTexture().needsRotate() ? 90 : 0, account.getCache().getHeadTexture()));
        }).addSegment("JVM", t -> {
            jre = option(t, "JDK/JRE Location", builder.jre);
            t.row();
            jvmOpts = option(t, "JVM Options", parseJvmOpts());
            t.row();
            launchArgs = option(t, "Launch Arguments");
        }).addSegment("Memory", t -> {
            t.add(theme.label("Minimum RAM")).expandX();
            xms = t.add(theme.textBox(parseXms(), MEMORY_FILTER)).expandX().widget();
            t.row();
            t.add(theme.label("Maximum RAM")).expandX();
            xmx = t.add(theme.textBox(parseXmx(), MEMORY_FILTER)).expandX().widget();
            t.row();
            t.add(theme.label("Newgen Size")).expandX();
            xmn = t.add(theme.textBox(parseXmn(), MEMORY_FILTER)).expandX().widget();
        }).addSegment("Window", t -> {
            t.add(theme.label("Window Width")).expandX();
            width = t.add(theme.textBox(getWidth(), NUMBER_FILTER)).expandX().widget();
            t.row();
            t.add(theme.label("Window Height")).expandX();
            height = t.add(theme.textBox(getHeight(), NUMBER_FILTER)).expandX().widget();
        }).addSegment("Meteor Client", t -> {
            t.add(theme.label("Activate Swarm")).expandX();
            swarmMode = t.add(theme.dropdown(SwarmMode.Off)).expandWidgetX().widget();
            t.row();
            t.add(theme.label("Swarm Host IP")).expandX();
            swarmIp = t.add(theme.textBox(getSwarmIp())).expandX().widget();
            t.row();
            t.add(theme.label("Swarm Host Port")).expandX();
            swarmPort = t.add(theme.textBox(getSwarmPort())).expandX().widget();
            t.row();

            t.add(theme.label("Deactivate Meteor")).expandX();
            deactivate = t.add(theme.checkbox(false)).expandCellX().widget();
        }).addSegment(t -> {
            launch = t.add(theme.button("Launch")).expandCellX().widget();
            launch.action = this::activate;
            info = t.add(theme.label("")).expandX().widget();
            WButton back = t.add(theme.button("Back")).right().expandCellX().widget();
            back.action = () -> mc.setScreen(new AccountsScreen(theme));
        });

        add(tb.build()).expandX();
    }

    private void activate() {
        info.set("");
        launch.minWidth = launch.width;
        launch.set("...");
        locked = true;

        MeteorExecutor.execute(() -> {
            builder.jre = jre.get();

            //Setup launch args
            builder.launchArgs.addAll(List.of(launchArgs.get().split(" ")));
            builder.modifyArg("--width", width.get());
            builder.modifyArg("--height", height.get());

            if (!swarmMode.get().equals(SwarmMode.Off)) {
                builder.modifyArg("--tokyo?swarmMode", swarmMode.get().name().toLowerCase(Locale.ROOT));
                builder.modifyArg("--tokyo?swarmIp", swarmIp.get());
                builder.modifyArg("--tokyo?swarmPort", swarmPort.get());
            }

            if (deactivate.checked && !builder.hasArg("--tokyo?deactivate")) builder.addArg("--tokyo?deactivate");

            builder.jvmOpts = modifyJvmOpts(builder.jvmOpts);

            builder.start();

            launch.minWidth = 0;
            launch.set("Launch");
            locked = false;
        });
    }

    public String getWidth() {
        return ArgsUtils.getArgOrElse("--width", () -> String.valueOf(mc.getWindow().getWidth()));
    }

    public String getHeight() {
        return ArgsUtils.getArgOrElse("--height", () -> String.valueOf(mc.getWindow().getHeight()));
    }

    private boolean isHidden(String opt) {
        return opt.contains(" ");
    }

    private String parseJvmOpts() {
        StringBuilder sb = new StringBuilder();
        for (var str : builder.jvmOpts) if (!isHidden(str) && !str.startsWith("-Xms") && !str.startsWith("-Xmx") && !str.startsWith("-Xmn")) sb.append(str).append(" ");
        return sb.toString().trim();
    }

    private String parseXms() {
        for (var token : builder.jvmOpts) {
            if (token.startsWith("-Xms")) return token.substring(4);
        }
        return "2048m";
    }

    private String parseXmx() {
        for (var token : builder.jvmOpts) {
            if (token.startsWith("-Xmx")) return token.substring(4);
        }
        return "2048m";
    }

    private String parseXmn() {
        for (var token : builder.jvmOpts) {
            if (token.startsWith("-Xmn")) return token.substring(4);
        }
        return "2048m";
    }

    private String getSwarmIp() {
        return ((ISwarm) Modules.get().get(Swarm.class)).getIpAddress().get();
    }

    private String getSwarmPort() {
        return ((ISwarm) Modules.get().get(Swarm.class)).getServerPort().toString();
    }

    private List<String> modifyJvmOpts(List<String> baseOpts) {
        List<String> newOpts = new ArrayList<>();

        for (var entry : baseOpts) if (isHidden(entry)) newOpts.add(entry);
        newOpts.addAll(Arrays.asList(jvmOpts.get().split(" ")));
        newOpts.add("-Xms" + xms.get());
        newOpts.add("-Xmx" + xmx.get());
        newOpts.add("-Xmn" + xmn.get());
        return newOpts;
    }

    private WTextBox option(WContainer root, String label) {
        return option(root, label, "");
    }

    private WTextBox option(WContainer root, String label, String placeholderText) {
        WHorizontalList list = root.add(theme.horizontalList()).expandX().widget();
        list.add(theme.label(label)).right().expandX();
        return list.add(theme.textBox(placeholderText)).minWidth(512).right().expandX().widget();
    }

    public enum SwarmMode {
        Off,
        Host,
        Worker
    }
}
