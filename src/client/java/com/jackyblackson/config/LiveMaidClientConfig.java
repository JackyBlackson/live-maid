package com.jackyblackson.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class LiveMaidClientConfig {
    public static final ConfigClassHandler<LiveMaidClientConfig> INSTANCE = ConfigClassHandler.createBuilder(LiveMaidClientConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("livemaid-client.json")).build())   // Change "modid" to your mod ID.
            .build();

    @SerialEntry
    private int bilibiliRoomId = -1;

    @SerialEntry
    private String masterName = "";

    @SerialEntry
    private String maidName = "";

    @SerialEntry
    private boolean sendDanmukuMsg = true;

    @SerialEntry
    private boolean sendGiftMsg = true;

    @SerialEntry
    private boolean sendSuperChatMsg = true;

    @SerialEntry
    private boolean sendEnterRoomMsg = true;

    @SerialEntry
    private boolean sendLikeMsg = true;

    @SerialEntry
    private boolean sendLiveStatusMsg = false;

    @SerialEntry
    private boolean thirdPersonFollow = true;

    @SerialEntry
    private double radius = 10.0d;

    @SerialEntry
    private boolean thirdPersonNoBlock = true;

    @SerialEntry
    private double positionLerpRatio = 0.3d;

    @SerialEntry
    private double angleLerpRatio = 0.1d;

    @SerialEntry
    private int retryLocateMasterCooldownFrames = 100*10;

    @SerialEntry
    private boolean viewControlByDanmuku = true;

    @SerialEntry
    private double controlPitchIncrement = 5d;

    @SerialEntry
    private double controlYawIncrement = 5d;

    @SerialEntry
    private double controlRadiusIncrement = 5d;

    @SerialEntry
    private String bilibiliWebpageCookie = "";

    public double radiusDelta = 0.0d;

    public double pitchDelta = 0.0d;

    public double yawDelta = 0.0d;

    public String getBilibiliWebpageCookie() {
        return bilibiliWebpageCookie;
    }

    public void setBilibiliWebpageCookie(String bilibiliWebpageCookie) {
        this.bilibiliWebpageCookie = bilibiliWebpageCookie;
        INSTANCE.save();
    }

    public double getControlPitchIncrement() {
        return controlPitchIncrement;
    }

    public void setControlPitchIncrement(double controlPitchIncrement) {
        this.controlPitchIncrement = controlPitchIncrement;
    }

    public double getControlYawIncrement() {
        return controlYawIncrement;
    }

    public void setControlYawIncrement(double controlYawIncrement) {
        this.controlYawIncrement = controlYawIncrement;
    }

    public double getControlRadiusIncrement() {
        return controlRadiusIncrement;
    }

    public void setControlRadiusIncrement(double controlRadiusIncrement) {
        this.controlRadiusIncrement = controlRadiusIncrement;
    }

    public boolean isViewControlByDanmuku() {
        return viewControlByDanmuku;
    }

    public void setViewControlByDanmuku(boolean viewControlByDanmuku) {
        this.viewControlByDanmuku = viewControlByDanmuku;
        INSTANCE.save();
    }

    public int getRetryLocateMasterCooldownFrames() {
        return retryLocateMasterCooldownFrames;
    }

    public void setRetryLocateMasterCooldownFrames(int retryLocateMasterCooldownFrames) {
        this.retryLocateMasterCooldownFrames = retryLocateMasterCooldownFrames;
        INSTANCE.save();
    }

    public double getPositionLerpRatio() {
        return positionLerpRatio;
    }

    public void setPositionLerpRatio(double positionLerpRatio) {
        this.positionLerpRatio = positionLerpRatio;
        INSTANCE.save();
    }

    public double getAngleLerpRatio() {
        return angleLerpRatio;
    }

    public void setAngleLerpRatio(double angleLerpRatio) {
        this.angleLerpRatio = angleLerpRatio;
        INSTANCE.save();
    }

    public boolean isThirdPersonNoBlock() {
        return thirdPersonNoBlock;
    }

    public void setThirdPersonNoBlock(boolean thirdPersonNoBlock) {
        this.thirdPersonNoBlock = thirdPersonNoBlock;
        INSTANCE.save();
    }

    public String getMaidName() {
        return maidName;
    }

    public void setMaidName(String maidName) {
        this.maidName = maidName;
        INSTANCE.save();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        INSTANCE.save();
    }

    public boolean isThirdPersonFollow() {
        return thirdPersonFollow;
    }

    public void setThirdPersonFollow(boolean thirdPersonFollow) {
        this.thirdPersonFollow = thirdPersonFollow;
        INSTANCE.save();
    }

    public boolean isSendDanmukuMsg() {
        return sendDanmukuMsg;
    }

    public void setSendDanmukuMsg(boolean sendDanmukuMsg) {
        this.sendDanmukuMsg = sendDanmukuMsg;
        INSTANCE.save();
    }

    public boolean isSendGiftMsg() {
        return sendGiftMsg;
    }

    public void setSendGiftMsg(boolean sendGiftMsg) {
        this.sendGiftMsg = sendGiftMsg;
        INSTANCE.save();
    }

    public boolean isSendSuperChatMsg() {
        return sendSuperChatMsg;
    }

    public void setSendSuperChatMsg(boolean sendSuperChatMsg) {
        this.sendSuperChatMsg = sendSuperChatMsg;
        INSTANCE.save();
    }

    public boolean isSendEnterRoomMsg() {
        return sendEnterRoomMsg;
    }

    public void setSendEnterRoomMsg(boolean sendEnterRoomMsg) {
        this.sendEnterRoomMsg = sendEnterRoomMsg;
        INSTANCE.save();
    }

    public boolean isSendLikeMsg() {
        return sendLikeMsg;
    }

    public void setSendLikeMsg(boolean sendLikeMsg) {
        this.sendLikeMsg = sendLikeMsg;
        INSTANCE.save();
    }

    public boolean isSendLiveStatusMsg() {
        return sendLiveStatusMsg;
    }

    public void setSendLiveStatusMsg(boolean sendLiveStatusMsg) {
        this.sendLiveStatusMsg = sendLiveStatusMsg;
        INSTANCE.save();
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
        INSTANCE.save();
    }

    public int getBilibiliRoomId() {
        return bilibiliRoomId;
    }

    public void setBilibiliRoomId(int bilibiliRoomId) {
        this.bilibiliRoomId = bilibiliRoomId;
        INSTANCE.save();
    }
}
