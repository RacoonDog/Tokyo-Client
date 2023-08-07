package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ChatManager extends Module {
    public static final ChatManager INSTANCE = new ChatManager();
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    public final Setting<Integer> chatDelay = sgGeneral.add(new IntSetting.Builder()
        .name("chat-delay")
        .description("Delay between each sent chat message.")
        .min(0)
        .sliderRange(0, 40)
        .defaultValue(20)
        .build()
    );

    private final Setting<Priority> priority = sgGeneral.add(new EnumSetting.Builder<Priority>()
        .name("priority")
        .description("Prioritize some messages above others.")
        .defaultValue(Priority.None)
        .onChanged(o -> this.lastIndex = 0)
        .build()
    );

    private final Setting<Boolean> stripIllegal = sgGeneral.add(new BoolSetting.Builder()
        .name("strip-illegal")
        .description("Prevent illegal characters from being sent")
        .defaultValue(true)
        .build()
    );

    private final List<Message> messageBuffer = new ArrayList<>();
    private int lastIndex = 0;
    private int timer = 0;
    private long disabledTimestamp = System.currentTimeMillis();

    private ChatManager() {
        super(Tokyo.CATEGORY, "chat-manager", "Acts as a priority queue system to prevent accidentally passing the kick threshold.");
    }

    @Override
    public void onActivate() {
        int ticksPassed = (int) (System.currentTimeMillis() - disabledTimestamp) / 50;
        if (ticksPassed < 0) timer = 0; //overflow
        else timer = Math.max(0, timer - ticksPassed);
    }

    @Override
    public void onDeactivate() {
        disabledTimestamp = System.currentTimeMillis();
    }

    @EventHandler
    private void onTick(TickEvent.Pre tickEvent) {
        timer = Math.max(timer - 1, 0);
        if (timer < 200) {
            //Priority
            if (priority.get() != Priority.None && lastIndex < messageBuffer.size()) {
                int index = lastIndex;
                while (index < messageBuffer.size()) {
                    Message message = messageBuffer.get(index++);
                    if (message.priority == priority.get()) {
                        lastIndex = index;
                        timer += chatDelay.get();
                        send(message);
                        messageBuffer.remove(index);
                    }
                }
            } else lastIndex = messageBuffer.size();

            //Send first if none is found
            if (!messageBuffer.isEmpty()) {
                if (lastIndex > 0) lastIndex--;
                timer += chatDelay.get();
                Message message = messageBuffer.remove(0);
                send(message);
            }
        }
    }

    public void queueSend(String string, Priority priority) {
        if (!isActive() || (timer < 200 && messageBuffer.isEmpty())) {
            timer += chatDelay.get();
            send(string, priority == Priority.Command);
        } else messageBuffer.add(new Message(string, priority));
    }

    public void sendNow(String message) {
        if (isActive()) timer += chatDelay.get();
        send(message, message.startsWith("/"));
    }

    private void send(Message message) {
        send(message.string, message.priority == Priority.Command);
    }

    private void send(String message, boolean isCommand) {
        if (stripIllegal.get()) {
            StringBuilder sb = new StringBuilder(message.length());
            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);
                if (SharedConstants.isValidChar(c)) sb.append(c);
                else sb.append(' ');
            }
            message = sb.toString();
        }

        if (isCommand) mc.player.networkHandler.sendChatCommand(message.substring(1));
        else mc.player.networkHandler.sendChatMessage(message);
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WButton clear = list.add(theme.button("Clear")).expandX().widget();
        clear.action = () -> {
            timer = 0;
            lastIndex = 0;
            disabledTimestamp = System.currentTimeMillis();
            messageBuffer.clear();
        };

        return list;
    }

    @Override
    public String getInfoString() {
        return timer + ", " + messageBuffer.size();
    }

    private record Message(String string, Priority priority) { }

    public enum Priority {
        None,
        Chat,
        Command,
        Meteor
    }
}
