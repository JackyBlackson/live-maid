package com.jackyblackson.message;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Master2MaidMessageProcessor {
    public static void parseMessage(String message) {
        int signCount = 0;
        int headerEndIndex = 0;
        boolean isCommand = false;
        for(int i = 0; i < message.length(); i++) {
            if(message.charAt(i) == '%') {
                signCount++;
            }
            if(signCount >= 4) {
                headerEndIndex = i;
                isCommand = true;
                break;
            }
        }

        if(!isCommand) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("[LIVE MAID] " + message));
        }

        String messageBody = message.substring(headerEndIndex + 1);
        System.out.println("Message Body From Master: " + messageBody);
        ChatBubble.showChatBubble(messageBody);
    }

}
