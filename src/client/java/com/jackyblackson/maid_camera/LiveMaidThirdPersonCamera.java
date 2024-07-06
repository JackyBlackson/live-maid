package com.jackyblackson.maid_camera;

import com.jackyblackson.config.LiveMaidClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import tech.ordinaryroad.live.chat.client.codec.bilibili.msg.DanmuMsgMsg;

import static com.jackyblackson.LiveMaidClient.getConfig;

public class LiveMaidThirdPersonCamera {

    public static void processDanmuku(DanmuMsgMsg msg) {
        if(getConfig().isViewControlByDanmuku()) {
            String content = msg.getContent().toLowerCase();
            switch (content){
                case "+":
                    addRadius(-getConfig().getControlRadiusIncrement());
                    break;
                case "-":
                    addRadius(getConfig().getControlRadiusIncrement());
                    break;
                case "a":
                    addYaw(getConfig().getControlYawIncrement());
                    break;
                case "d":
                    addYaw(-getConfig().getControlYawIncrement());
                    break;
                case "w":
                    addPitch(getConfig().getControlPitchIncrement());
                    break;
                case "s":
                    addPitch(-getConfig().getControlPitchIncrement());
                    break;
            }

        }
    }

    public static void addPitch(double amount) {
        getConfig().pitchDelta += amount;
    }

    public static void setPitch(double amount) {
        getConfig().pitchDelta = amount;
    }

    public static void addYaw(double amount) {
        getConfig().yawDelta += amount;
    }

    public static void setYaw(double amount) {
        getConfig().yawDelta = amount;
    }

    public static void addRadius(double amount) {
        getConfig().radiusDelta += amount;
    }

    public static void setRadius(double amount) {
        getConfig().radiusDelta = amount;
    }

    private static int retryCounter = getConfig().getRetryLocateMasterCooldownFrames();

    public static void updateCameraMinecraft3rdView(ClientPlayerEntity player) {
        String targetPlayerName = getConfig().getMasterName();
        double CAMERA_DISTANCE = getConfig().getRadius();

        if(targetPlayerName.isBlank()) {
            return;
        }

        PlayerEntity targetPlayer = player.clientWorld.getPlayers().stream()
                .filter(p -> p.getName().getString().equals(targetPlayerName))
                .findFirst()
                .orElse(null);

        if (targetPlayer == null) {
            if(retryCounter % getConfig().getRetryLocateMasterCooldownFrames() == 0) {
                player.sendMessage(Text.literal("[LIVE MAID] 找不到目标玩家 \"" + targetPlayerName + "\" 哦~"));
                retryCounter = 1;
            } else {
                retryCounter += 1;
            }
            return; // 目标玩家不存在，退出
        }

        retryCounter = 0;

        // 获取目标玩家的坐标和视角
        Vec3d targetPos = targetPlayer.getPos();
        float targetYaw = targetPlayer.getYaw();
        float targetPitch = targetPlayer.getPitch();

        double pitchDelta = getConfig().pitchDelta;
        double yawDelta = getConfig().yawDelta;

        double finalPitch = targetPitch + pitchDelta;
        double finalYaw = targetYaw + yawDelta;

        double radius = CAMERA_DISTANCE + getConfig().radiusDelta;

        // 计算摄像机位置：Pt-Pc 向量平行于目标玩家的视线向量，且与目标玩家保持目标距离
        double offsetX = - radius * Math.cos(Math.toRadians(finalPitch)) * Math.sin(Math.toRadians(finalYaw));
        double offsetY = - radius * Math.sin(Math.toRadians(finalPitch));
        double offsetZ = radius * Math.cos(Math.toRadians(finalPitch)) * Math.cos(Math.toRadians(finalYaw));

        Vec3d cameraPos = new Vec3d(targetPos.x - offsetX, targetPos.y - offsetY, targetPos.z - offsetZ); // 假设摄像机高度在玩家头部上方

        // 计算速度向量
        Vec3d currentPos = player.getPos();
        Vec3d velocity = cameraPos.subtract(currentPos).multiply(getConfig().getPositionLerpRatio()); // 速度向量，0.1 是一个平滑因子

        // 设置玩家速度
        player.setVelocity(velocity.x, velocity.y, velocity.z);
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY(), player.getZ(), false));

        // 计算摄像机玩家的视线向量，使其始终看向目标玩家
        Vec3d lookVec = targetPos.subtract(currentPos).normalize();

        double lerpDelta = getConfig().getAngleLerpRatio();
        double smoothYaw = lerp(player.getYaw(), targetYaw, lerpDelta);
        double smoothPitch = lerp(player.getPitch(), targetPitch, lerpDelta);

        player.setYaw((float) smoothYaw);
        player.setPitch((float) smoothPitch);
    }

    private static double lerp(double start, double end, double delta) {
        return start + delta * (end - start);
    }
}
