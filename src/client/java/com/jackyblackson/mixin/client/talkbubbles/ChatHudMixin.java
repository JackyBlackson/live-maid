package com.jackyblackson.mixin.client.talkbubbles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jackyblackson.LiveMaidClient;
import com.jackyblackson.message.Master2MaidMessageProcessor;
import com.jackyblackson.message.MessageUtilities;
import com.jackyblackson.talkbubbles.TalkBubbles;
import com.jackyblackson.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;
import com.jackyblackson.utilities.IOUtilities;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Shadow
    @Final
    @Mutable
    private MinecraftClient client;

    // onChatMessage is now done in MessageHandler.class
    @Inject(method = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void addMessageMixin(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo info) {
        if (MessageUtilities.isReceivedCommand(message)) {
            String stringMessage = message.getString();
            int firstQuoteIndex = 0;
            for(int i = 0; i < stringMessage.length(); i++) {
                if(stringMessage.charAt(i) == '"') {
                    firstQuoteIndex = i;
                    break;
                }
            }
            String msgFromMaster = stringMessage.substring(firstQuoteIndex + 1, stringMessage.length() - 1);
            IOUtilities.getLogger().info("Received COMMAND FROM MASTER: " + msgFromMaster);
            Master2MaidMessageProcessor.parseMessage(msgFromMaster);
            info.cancel();
        }
        if (MessageUtilities.isCommandFeedback(message)) {
            info.cancel();
        }
        if (client != null && client.player != null) {
            String detectedSenderName = extractSender(message);
            if (!detectedSenderName.isEmpty()) {
                UUID senderUUID = this.client.getSocialInteractionsManager().getUuid(detectedSenderName);

                List<AbstractClientPlayerEntity> list = client.world.getEntitiesByClass(AbstractClientPlayerEntity.class, client.player.getBoundingBox().expand(LiveMaidClient.CONFIG.chatRange),
                        EntityPredicates.EXCEPT_SPECTATOR);

                if (!LiveMaidClient.CONFIG.showOwnBubble) {
                    list.remove(client.player);
                }
                for (int i = 0; i < list.size(); i++)
                    if (list.get(i).getUuid().equals(senderUUID)) {
                        String stringMessage = message.getString();
                        stringMessage = stringMessage.replaceFirst("[\\s\\S]*" + detectedSenderName + "([^\\w§]|(§.)?)+\\s+", "");
                        String[] string = stringMessage.split(" ");
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
                        break;
                    }
            }
        }

    }

    private String extractSender(Text text) {
        String[] words = text.getString().split("(§.)|[^\\w§]+");
        String[] parts = text.toString().split("key='");

        if (parts.length > 1) {
            String translationKey = parts[1].split("'")[0];
            if (translationKey.contains("commands")) {
                return "";
            } else if (translationKey.contains("advancement")) {
                return "";
            }
        }

        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) {
                continue;
            }
            if (TalkBubbles.CONFIG.maxUUIDWordCheck != 0 && i >= TalkBubbles.CONFIG.maxUUIDWordCheck) {
                return "";
            }

            UUID possibleUUID = this.client.getSocialInteractionsManager().getUuid(words[i]);
            if (possibleUUID != Util.NIL_UUID) {
                return words[i];
            }
        }

        return "";
    }
}
