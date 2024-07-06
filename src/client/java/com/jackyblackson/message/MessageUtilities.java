package com.jackyblackson.message;

import com.jackyblackson.LiveMaidClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MessageUtilities {
    public static boolean isReceivedCommand(Text message) {
        String stringedMessage = message.toString();
        return
                stringedMessage.contains("key='commands.message.display.incoming'")
                        && (
                            stringedMessage.contains("literal{" + LiveMaidClient.getConfig().getMasterName() + "}")
                            ||
                            stringedMessage.contains("literal{" + LiveMaidClient.getConfig().getMaidName() + "}")
                );
    }

    public static boolean isCommandFeedback(Text message) {
        String stringedMessage = message.toString();
        return stringedMessage.contains("key='commands.message.display.outgoing'")
                && (
                    stringedMessage.contains("literal{" + LiveMaidClient.getConfig().getMasterName() + "}")
                    ||
                    stringedMessage.contains("literal{" + LiveMaidClient.getConfig().getMaidName() + "}")
        );
    }
}
