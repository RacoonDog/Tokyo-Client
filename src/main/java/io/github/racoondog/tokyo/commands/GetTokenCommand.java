package io.github.racoondog.tokyo.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import meteordevelopment.meteorclient.commands.Command;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class GetTokenCommand extends Command {
    public GetTokenCommand() {
        super("getToken", "Outputs your account's access token in chat");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(this::getToken);
    }

    private int getToken(CommandContext<CommandSource> ctx) {
        String token = mc.getSession().getAccessToken();
        info(Text.literal("[Click to copy token]").setStyle(Style.EMPTY.withFormatting(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, token))));

        return SINGLE_SUCCESS;
    }
}
