package com.jackyblackson.message;

import com.jackyblackson.LiveMaidClient;
import com.jackyblackson.mixin.client.ChatScreenMixin;
import com.jackyblackson.talkbubbles.TalkBubbles;
import com.jackyblackson.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatBubble {

    public static void showChatBubble(String message) {
        displayTitle(message);
    }

    private static void displayTitle(String displayMessage) {
        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.player != null) {
//            AbstractClientPlayerEntityAccessor mixinPlayer = (AbstractClientPlayerEntityAccessor) client.player;
//            mixinPlayer.live_maid$setChatText(List.of(message), mixinPlayer.live_maid$getOldAge(), mixinPlayer.live_maid$getWidth(), mixinPlayer.live_maid$getHeight());
//        }
        Text message = Text.literal(displayMessage);
        List<AbstractClientPlayerEntity> list = client.world.getEntitiesByClass(AbstractClientPlayerEntity.class, client.player.getBoundingBox().expand(LiveMaidClient.CONFIG.chatRange),
                EntityPredicates.EXCEPT_SPECTATOR);
        for (int i = 0; i < list.size(); i++){

            String stringMessage = message.getString();
            String[] string = new String[1];
            string[0] = stringMessage;
            List<String> stringList = new ArrayList<>();
            String stringCollector = "";

            int width = 0;
            int height = 0;
            for (int u = 0; u < string.length; u++) {
                if (client.textRenderer.getWidth(stringCollector) < LiveMaidClient.CONFIG.maxChatWidth
                        && client.textRenderer.getWidth(stringCollector) + client.textRenderer.getWidth(string[u]) <= TalkBubbles.CONFIG.maxChatWidth) {
                    stringCollector = stringCollector + " " + string[u];
                    if (u == string.length - 1) {
                        stringList.add(stringCollector);
                        height++;
                        if (width < client.textRenderer.getWidth(stringCollector)) {
                            width = client.textRenderer.getWidth(stringCollector);
                        }
                    }
                } else {
                    stringList.add(stringCollector);

                    height++;
                    if (width < client.textRenderer.getWidth(stringCollector)) {
                        width = client.textRenderer.getWidth(stringCollector);
                    }

                    stringCollector = string[u];

                    if (u == string.length - 1) {
                        stringList.add(stringCollector);
                        height++;
                        if (width < client.textRenderer.getWidth(stringCollector)) {
                            width = client.textRenderer.getWidth(stringCollector);
                        }
                    }
                }
            }

            if (width % 2 != 0) {
                width++;
            }
            ((AbstractClientPlayerEntityAccessor) list.get(i)).live_maid$setChatText(stringList, list.get(i).age, width, height);
        }
    }
}
