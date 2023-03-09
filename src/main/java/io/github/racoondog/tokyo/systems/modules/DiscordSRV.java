package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.utils.webhook.WebhookContent;
import io.github.racoondog.tokyo.utils.webhook.WebhookHandler;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Runtime;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class DiscordSRV extends Module {
    public static final DiscordSRV INSTANCE = new DiscordSRV();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgFilter = this.settings.createGroup("Filter");

    // General
    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.Webhook)
        .onChanged(o -> changeMode())
        .build()
    );

    // Bot
    private final Setting<String> botToken = sgGeneral.add(new StringSetting.Builder()
        .name("bot-token")
        .defaultValue("enter bot token")
        .visible(() -> mode.get() == Mode.Bot)
        .onChanged(o -> requireRebuild = true)
        .build()
    );

    private final Setting<String> targetChannelId = sgGeneral.add(new StringSetting.Builder()
        .name("target-channel-id")
        .defaultValue("689199457547321379")
        .visible(() -> mode.get() == Mode.Bot)
        .onChanged(o -> channel = null)
        .build()
    );

    // Webhook

    private final Setting<String> webhookUrl = sgGeneral.add(new StringSetting.Builder()
        .name("webhook-url")
        .defaultValue("enter webhook url")
        .visible(() -> mode.get() == Mode.Webhook)
        .build()
    );

    private final Setting<String> webhookUsername = sgGeneral.add(new StringSetting.Builder()
        .name("webhook-username")
        .defaultValue("Tokyo Client")
        .visible(() -> mode.get() == Mode.Webhook)
        .build()
    );

    private final Setting<String> webhookAvatarUrl = sgGeneral.add(new StringSetting.Builder()
        .name("webhook-avatar-url")
        .defaultValue("https://media.discordapp.net/attachments/1061188655307304962/1077761281823621140/letsfuckinggo_small.png")
        .visible(() -> mode.get() == Mode.Webhook)
        .build()
    );

    // Filter

    private final Setting<List<String>> regexFilters = sgFilter.add(new StringListSetting.Builder()
        .name("regex-filters")
        .defaultValue("^\\[Baritone] ")
        .onChanged(o -> recompileRegex())
        .build()
    );

    private final Setting<Boolean> stripFormatting = sgFilter.add(new BoolSetting.Builder()
        .name("strip-formatting")
        .defaultValue(true)
        .build()
    );

    private final Pattern formattingRegex = Pattern.compile("§[0-9a-fklmnor]");
    private final List<Pattern> compiledRegexFilters = new ArrayList<>();
    private boolean requireRebuild = true;
    @Nullable
    private JDA bot = null;
    @Nullable
    private MessageChannel channel = null;

    private DiscordSRV() {
        super(Tokyo.CATEGORY, "discord-srv", "Turn your client into a Discord bot/webhook.");
    }

    @Override
    public void onActivate() {
        if (mode.get() == Mode.Bot) rebuildBot();
    }

    @Override
    public void onDeactivate() {
        if (mode.get() == Mode.Bot) shutdownBot();
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        String message = event.getMessage().getString();

        if (stripFormatting.get()) message = formattingRegex.matcher(message).replaceAll("");

        for (var filter : compiledRegexFilters) {
            if (filter.matcher(message).find()) return;
        }

        if (mode.get() == Mode.Bot) {
            if (bot == null || requireRebuild) rebuildBot();
            if (channel == null) getChannel();
            if (channel == null) return;

            try {
                channel.sendMessage(message);
            } catch (Throwable t) {
                warning("Could not send message.");
            }
        } else {
            if (!webhookUrl.get().equals("enter webhook url")) {
                String finalMessage = message;
                MeteorExecutor.execute(() -> {
                    try {
                        WebhookHandler.send(webhookUrl.get(), new WebhookContent(
                            finalMessage,
                            webhookUsername.get(),
                            webhookAvatarUrl.get(),
                            false
                        ));
                    } catch (Exception e) {
                        warning("Error sending webhook.");
                        e.printStackTrace();
                        toggle();
                    }
                });
            }
        }
    }

    private void rebuildBot() {
        requireRebuild = false;

        if (bot != null) bot.shutdownNow();
        try {
            bot = JDABuilder.createLight(botToken.get()).build();
            getChannel();
        } catch (InvalidTokenException e) {
            warning("Could not login: Invalid token.");
            toggle();
        }
    }

    private void getChannel() {
        try {
            MessageChannel messageChannel = bot.getChannelById(MessageChannel.class, targetChannelId.get());
            if (messageChannel == null) warning("Channel %s is not a valid message channel.", targetChannelId.get());
            channel = messageChannel;
        } catch (IllegalArgumentException e) {
            warning("Could not find channel %s.", targetChannelId.get());
        }
    }

    private void changeMode() {
        if (mode.get() == Mode.Bot) {
            requireRebuild = true;
        } else {
            shutdownBot();
        }
    }

    private void shutdownBot() {
        if (bot != null) {
            bot.shutdownNow();
            bot = null;
        }
    }

    private void recompileRegex() {
        compiledRegexFilters.clear();

        for (var filter : regexFilters.get()) {
            compiledRegexFilters.add(Pattern.compile(filter));
        }
    }

    public enum Mode {
        Bot,
        Webhook
    }
}
