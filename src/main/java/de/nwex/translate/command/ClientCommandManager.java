package de.nwex.translate.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nwex.translate.Chat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

import static de.nwex.translate.TextUtil.stackTraceToString;

public class ClientCommandManager
{
    private static Set<String> clientSideCommands = new HashSet<>();

    public static void clearClientSideCommands()
    {
        clientSideCommands.clear();
    }

    public static void addClientSideCommand(String name)
    {
        clientSideCommands.add(name);
    }

    public static boolean isClientSideCommand(String name)
    {
        return clientSideCommands.contains(name);
    }

    public static int executeCommand(StringReader reader, String command)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        try
        {
            if(player != null)
            {
                return player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
            }
        }
        catch(CommandException e)
        {
            Chat.error(e.getTextMessage().asString());
        }
        catch(CommandSyntaxException e)
        {
            Chat.error(e.getRawMessage().getString());

            if(e.getInput() != null && e.getCursor() >= 0)
            {
                int cursor = Math.min(e.getCursor(), e.getInput().length());

                Text text = new LiteralText("").formatted(Formatting.GRAY).styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));

                if(cursor > 10)
                {
                    text.append("...");
                }

                text.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));

                if(cursor < e.getInput().length())
                {
                    text.append(new LiteralText(e.getInput().substring(cursor)).formatted(Formatting.RED, Formatting.UNDERLINE));
                }

                text.append(new TranslatableText("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));

                Chat.error(text.asString());
            }
        }
        catch(Exception exception)
        {
            Chat.error(new LiteralText("Command"), new LiteralText(exception.getClass().getName() + ": " + exception.getMessage()).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(stackTraceToString(exception)).formatted(Formatting.RED)))));
        }

        return 1;
    }
}
