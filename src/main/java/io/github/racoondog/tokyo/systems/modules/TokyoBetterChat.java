package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.mixin.IMutableText;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TokyoBetterChat extends Module {
    public static final TokyoBetterChat INSTANCE = new TokyoBetterChat();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Boolean> stripFancy = sgGeneral.add(new BoolSetting.Builder()
        .name("strip-fancy")
        .defaultValue(true)
        .build()
    );

    private final Char2CharMap unicodeTable = new Char2CharOpenHashMap();

    private TokyoBetterChat() {
        super(Tokyo.CATEGORY, "tokyo-better-chat", "yeaj");

        String[] regularCharacters = "abcdefghijklmnopqrstuvwxyz".split("");

        buildUnicodeTable("ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩqʀꜱᴛᴜᴠᴡxyᴢ", regularCharacters);
        buildUnicodeTable("ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ", regularCharacters);
        buildUnicodeTable("ɐqɔpǝɟɓɥıɾʞlɯuodbɹsʇnʌʍxʎz", regularCharacters);
        buildUnicodeTable("ɒdɔbɘᎸǫʜiꞁʞ|mᴎoqpɿꙅƚuvwxʏƹ", regularCharacters);
        buildUnicodeTable("ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖqʳˢᵗᵘᵛʷˣʸᶻ", regularCharacters);
        buildUnicodeTable("ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ", regularCharacters);
        buildUnicodeTable("ᗩᗷᑕᗪEᖴGᕼIᒍKᒪᗰᑎOᑭᑫᖇᔕTᑌᐯᗯ᙭Yᘔ", regularCharacters);
        buildUnicodeTable("ᴀʙᴄᴅᴇꜰɢhɪjᴋʟᴍɴᴏqʀꜱᴛuvwxyz", regularCharacters);
    }

    private void buildUnicodeTable(String str, String[] reg) {
        String[] fancyCharacters = str.split("");
        for (int i = 0; i < fancyCharacters.length; i++) {
            String regStr = reg[i];
            char fancyChar = fancyCharacters[i].charAt(0);

            if (!arrayContains(regStr, reg) && !unicodeTable.containsKey(fancyChar)) unicodeTable.put(fancyChar, regStr.charAt(0));
        }
    }

    private boolean arrayContains(String str, String[] arr) {
        for (var arrElement : arr) {
            if (str.equals(arrElement)) return true;
        }
        return false;
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        if (stripFancy.get()) stripFancy(event.getMessage());
    }

    private void stripFancy(Text text) {
        for (var sibling : text.getSiblings()) stripFancy(sibling);

        if (text instanceof MutableText mutableText && mutableText.getContent() instanceof LiteralTextContent literalTextContent) {
            StringBuilder sb = new StringBuilder(literalTextContent.string().length());
            for (var c : literalTextContent.string().toCharArray()) {
                if (unicodeTable.containsKey(c)) sb.append(unicodeTable.get(c));
                else sb.append(c);
            }
            ((IMutableText) mutableText).tokyo$setContent(new LiteralTextContent(sb.toString()));
        }
    }
}
