package de.nwex.translate.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import de.nwex.translate.Chat;
import de.nwex.translate.ChatTranslate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler
{
    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Shadow
    private MinecraftClient client;

    private Boolean once = true;


    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageS2CPacket chatMessageS2CPacket, CallbackInfo ci)
    {
        Pattern coolPattern = Pattern.compile("^§.§.§.([A-Z]+)§r §.([A-Za-z_0-9]{1,16})§.: §r(.+)$");
        Pattern simplyPattern = Pattern.compile("^<([A-Za-z_0-9]{1,16})> (.+)$");

        Matcher coolMatcher = coolPattern.matcher(chatMessageS2CPacket.getMessage().asFormattedString().replace("\n", "").trim());
        Matcher simplyMatcher = simplyPattern.matcher(chatMessageS2CPacket.getMessage().asFormattedString().replace("\n", "").trim());


        if(coolMatcher.matches())
        {
            this.client.inGameHud.addChatMessage(chatMessageS2CPacket.getLocation(), chatMessageS2CPacket.getMessage()
                .setStyle(new Style()
                    .setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to translate"))
                    )
                    .setClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/translate \"" + coolMatcher.group(3) + "\"")
                    )
                )
            );
        }
        else if(simplyMatcher.matches())
        {
            this.client.inGameHud.addChatMessage(chatMessageS2CPacket.getLocation(), chatMessageS2CPacket.getMessage()
                .setStyle(new Style()
                    .setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to translate"))
                    )
                    .setClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/translate \"" + simplyMatcher.group(2) + "\"")
                    )
                )
            );
        }
        else
        {
            this.client.inGameHud.addChatMessage(chatMessageS2CPacket.getLocation(), chatMessageS2CPacket.getMessage()
                .setStyle(new Style()
                    .setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("No translation available, unable to parse message"))
                    )
                )
            );
        }

        ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        if(once)
        {
            once = false;

            new Thread(() ->
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }

                Chat.print(new LiteralText("Loaded"), new LiteralText("Using Google Translate"));
            }).start();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(MinecraftClient mc, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo ci)
    {
        ChatTranslate.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "onCommandTree", at = @At("TAIL"))
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci)
    {
        ChatTranslate.registerCommands((CommandDispatcher<ServerCommandSource>) (Object) commandDispatcher);
    }
}
