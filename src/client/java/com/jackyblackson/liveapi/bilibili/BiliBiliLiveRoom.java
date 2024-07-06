package com.jackyblackson.liveapi.bilibili;

import com.jackyblackson.config.LiveMaidClientConfig;
import com.jackyblackson.maid_camera.LiveMaidThirdPersonCamera;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import tech.ordinaryroad.live.chat.client.bilibili.client.BilibiliLiveChatClient;
import tech.ordinaryroad.live.chat.client.bilibili.config.BilibiliLiveChatClientConfig;
import tech.ordinaryroad.live.chat.client.bilibili.listener.IBilibiliMsgListener;
import tech.ordinaryroad.live.chat.client.bilibili.netty.handler.BilibiliBinaryFrameHandler;
import tech.ordinaryroad.live.chat.client.codec.bilibili.msg.*;
import tech.ordinaryroad.live.chat.client.commons.client.enums.ClientStatusEnums;

import static com.jackyblackson.message.Send2MasterUtilities.sendPlainMessage;

public class BiliBiliLiveRoom {
    private final int roomId;

    FabricClientCommandSource source;

    private final LiveMaidClientConfig clientConfig;

    private BilibiliLiveChatClient bilibiliLiveChatClient;

    public static LiveMaidClientConfig getConfig() {
        return LiveMaidClientConfig.INSTANCE.instance();
    }

    public BiliBiliLiveRoom(int roomId, FabricClientCommandSource source) {
        this.source = source;
        this.roomId = roomId;
        this.clientConfig = LiveMaidClientConfig.INSTANCE.instance();
        LiveMaidClientConfig.INSTANCE.instance().setBilibiliRoomId(roomId);
    }

    public void startRoom() {
        // String cookie = System.getenv("cookie");
        // 1. 创建配置
        var configBuilder = BilibiliLiveChatClientConfig.builder()
//                // TODO 消息转发地址
//                .forwardWebsocketUri("")
//                // TODO 浏览器Cookie
//                .cookie(cookie)
                // TODO 直播间id（支持短id）
                .roomId(this.roomId);
        if(!getConfig().getBilibiliWebpageCookie().isBlank()) {
            configBuilder.cookie(getConfig().getBilibiliWebpageCookie());
        }

        BilibiliLiveChatClientConfig config = configBuilder.build();

        // 2. 创建Client并传入配置、添加消息回调
        bilibiliLiveChatClient = new BilibiliLiveChatClient(config, new IBilibiliMsgListener() {
            @Override
            public void onDanmuMsg(BilibiliBinaryFrameHandler binaryFrameHandler, DanmuMsgMsg msg) {
                LiveMaidThirdPersonCamera.processDanmuku(msg);
                if(!getConfig().isSendDanmukuMsg()) return;
                IBilibiliMsgListener.super.onDanmuMsg(binaryFrameHandler, msg);
                sendPlainMessage((String.format("%s 收到弹幕 %s %s(%s)：%s", binaryFrameHandler.getRoomId(), msg.getBadgeLevel() != 0 ? msg.getBadgeLevel() + msg.getBadgeName() : "", msg.getUsername(), msg.getUid(), msg.getContent())));

            }

            @Override
            public void onGiftMsg(BilibiliBinaryFrameHandler binaryFrameHandler, SendGiftMsg msg) {
                if(!getConfig().isSendGiftMsg()) return;
                IBilibiliMsgListener.super.onGiftMsg(binaryFrameHandler, msg);
                sendPlainMessage((String.format("%s 收到礼物 %s %s(%s) %s %s(%s)x%s(%s)", binaryFrameHandler.getRoomId(), msg.getBadgeLevel() != 0 ? msg.getBadgeLevel() + msg.getBadgeName() : "", msg.getUsername(), msg.getUid(), msg.getData().getAction(), msg.getGiftName(), msg.getGiftId(), msg.getGiftCount(), msg.getGiftPrice())));
            }

            @Override
            public void onSuperChatMsg(BilibiliBinaryFrameHandler binaryFrameHandler, SuperChatMessageMsg msg) {
                if(!getConfig().isSendSuperChatMsg()) return;
                IBilibiliMsgListener.super.onSuperChatMsg(binaryFrameHandler, msg);
                sendPlainMessage((String.format("%s 收到醒目留言 %s(%s)：%s", binaryFrameHandler.getRoomId(), msg.getUsername(), msg.getUid(), msg.getContent())));
            }

            @Override
            public void onEnterRoomMsg(InteractWordMsg msg) {
                if(!getConfig().isSendEnterRoomMsg()) return;
                sendPlainMessage((String.format("%s %s(%s) 进入直播间", msg.getBadgeLevel() != 0 ? msg.getBadgeLevel() + msg.getBadgeName() : "", msg.getUsername(), msg.getUid())));
            }

            @Override
            public void onLikeMsg(BilibiliBinaryFrameHandler binaryFrameHandler, LikeInfoV3ClickMsg msg) {
                if(!getConfig().isSendLikeMsg()) return;
                IBilibiliMsgListener.super.onLikeMsg(binaryFrameHandler, msg);
                sendPlainMessage((String.format("%s 收到点赞 %s %s(%s)", binaryFrameHandler.getRoomId(), msg.getBadgeLevel() != 0 ? msg.getBadgeLevel() + msg.getBadgeName() : "", msg.getUsername(), msg.getUid())));
            }

            @Override
            public void onLiveStatusMsg(BilibiliBinaryFrameHandler binaryFrameHandler, BilibiliLiveStatusChangeMsg msg) {
                if(!getConfig().isSendLiveStatusMsg()) return;
                IBilibiliMsgListener.super.onLiveStatusMsg(binaryFrameHandler, msg);
                sendPlainMessage((String.format("%s 状态变化 %s", binaryFrameHandler.getRoomId(), msg.getLiveStatusAction())));
            }
        });

        // 3. 开始监听直播间
        bilibiliLiveChatClient.connect();
    }

    public void stopRoom() {
        if( this.bilibiliLiveChatClient != null && this.bilibiliLiveChatClient.getStatus().equals(ClientStatusEnums.CONNECTED)) {
            this.bilibiliLiveChatClient.disconnect();
            this.bilibiliLiveChatClient.destroy();
        }
    }
}