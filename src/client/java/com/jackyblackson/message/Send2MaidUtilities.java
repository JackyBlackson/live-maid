package com.jackyblackson.message;

import com.jackyblackson.config.LiveMaidClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class Send2MaidUtilities {

    public static void sendChat(ClientPlayerEntity player, String message) {
        player.networkHandler.sendChatMessage(message);

    }

    public static void executeCommand(ClientPlayerEntity player, String command){
//        if (command.startsWith("/")) {
//            command = command.substring(1);
//            sendChat(player, command);
//        } else {
        player.networkHandler.sendChatCommand(command);
//        }
    }
    public static void sendPlainMessage(String message) {
        var player = MinecraftClient.getInstance().player;
        var maid = LiveMaidClientConfig.INSTANCE.instance().getMaidName();
        var command = "tell " + maid + " \"" + message + "\"";
//        var command = "tell " + "Jacky_Blackson" + " \"" + message + "\"";
        //player.sendMessage(Text.literal("[Live Maid Debug] COMMAND IS " + command));
        assert player != null;
        executeCommand(player, command);
    }
}
