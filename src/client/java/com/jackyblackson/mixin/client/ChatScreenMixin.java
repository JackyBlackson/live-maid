package com.jackyblackson.mixin.client;

import com.jackyblackson.message.Send2MaidUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.jackyblackson.LiveMaidClient.getConfig;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow
    protected TextFieldWidget chatField;

    @Inject(method = "onChatFieldUpdate", at = @At("TAIL"))
    private void handleChatFieldUpdate(CallbackInfo info) {
        String chatText = this.chatField.getText();
        // 在这里处理聊天输入
        if (!getConfig().getMaidName().isBlank() && !chatText.isBlank()) {
            System.out.println("玩家正在输入: " + chatText);
            assert MinecraftClient.getInstance().player != null;
            Send2MaidUtilities.sendPlainMessage("%%chat%%" + chatText);
        }
    }
}
