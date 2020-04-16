package de.nwex.translate;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class Chat
{
    public static void print(Text prefix, Text message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(ChatTranslate.DARK))
            .append(new LiteralText("chatTranslate").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText("] ").formatted(ChatTranslate.DARK))
            .append(prefix.formatted(ChatTranslate.BASE))
            .append(new LiteralText(" > ").formatted(ChatTranslate.ACCENT))
            .append(message);

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void print(String prefix, String message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(ChatTranslate.DARK))
            .append(new LiteralText("chatTranslate").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText("] ").formatted(ChatTranslate.DARK))
            .append(new LiteralText(prefix).formatted(ChatTranslate.BASE))
            .append(new LiteralText(" > ").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText(message));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void print(String message)
    {
        print("Log", message);
    }

    public static void warn(String prefix, String message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(ChatTranslate.DARK))
            .append(new LiteralText("chatTranslate").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText("] ").formatted(ChatTranslate.DARK))
            .append(new LiteralText(prefix).formatted(ChatTranslate.WARN))
            .append(new LiteralText(" > ").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText(message).formatted(ChatTranslate.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void warn(String message)
    {
        print("Warn", message);
    }

    public static void error(Text prefix, Text message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(ChatTranslate.DARK))
            .append(new LiteralText("chatTranslate").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText("] ").formatted(ChatTranslate.DARK))
            .append(prefix.formatted(ChatTranslate.ERROR))
            .append(new LiteralText(" > ").formatted(ChatTranslate.ACCENT))
            .append(message.formatted(ChatTranslate.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void error(String prefix, String message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(ChatTranslate.DARK))
            .append(new LiteralText("chatTranslate").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText("] ").formatted(ChatTranslate.DARK))
            .append(new LiteralText(prefix).formatted(ChatTranslate.ERROR))
            .append(new LiteralText(" > ").formatted(ChatTranslate.ACCENT))
            .append(new LiteralText(message).formatted(ChatTranslate.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void error(String message)
    {
        print("Error", message);
    }
}
