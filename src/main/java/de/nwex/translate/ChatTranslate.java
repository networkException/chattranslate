package de.nwex.translate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.nwex.translate.api.web.Request;
import de.nwex.translate.command.ClientCommandManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ChatTranslate implements ClientModInitializer
{
    public static Formatting BASE = Formatting.GRAY;
    public static Formatting ACCENT = Formatting.BLUE;
    public static Formatting HIGHLIGHT = Formatting.WHITE;
    public static Formatting DARK = Formatting.DARK_GRAY;
    public static Formatting WARN = Formatting.YELLOW;
    public static Formatting ERROR = Formatting.RED;

    @Override
    public void onInitializeClient()
    {
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        ClientCommandManager.addClientSideCommand("translate");

        dispatcher.register(literal("translate")
            .then(argument("text", StringArgumentType.string())
                .executes((context) ->
                {
                    String text = StringArgumentType.getString(context, "text");

                    new Thread(() ->
                    {
                        try
                        {
                            JSONArray response = new JSONArray(Request.call("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8.toString())));

                            Chat.print(new LiteralText(response.getString(2).toUpperCase()), new LiteralText(response.getJSONArray(0).getJSONArray(0).getString(0)).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(response.getJSONArray(0).getJSONArray(0).getString(1))))));
                        }
                        catch(UnsupportedEncodingException e)
                        {
                            Chat.error(new LiteralText("Error"), new LiteralText("Error translating text: " + e.getMessage()).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(TextUtil.stackTraceToString(e))))));
                        }
                    }).start();

                    return 1;
                }))
            .executes((context) ->
            {
                Chat.error("No text specified");

                return -1;
            }));
    }
}
