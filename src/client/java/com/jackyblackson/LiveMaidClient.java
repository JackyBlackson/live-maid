package com.jackyblackson;

import com.jackyblackson.config.LiveMaidClientConfig;
import com.jackyblackson.liveapi.bilibili.BiliBiliLiveRoom;
import com.jackyblackson.maid_camera.LiveMaidThirdPersonCamera;
import com.jackyblackson.message.Master2MaidMessageProcessor;
import com.jackyblackson.talkbubbles.config.TalkBubblesConfig;
import com.jackyblackson.utilities.IOUtilities;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class LiveMaidClient implements ClientModInitializer {

	private BiliBiliLiveRoom biliBiliLiveRoom;

	public static TalkBubblesConfig CONFIG = new TalkBubblesConfig();

	@Override
	public void onInitializeClient() {
		LiveMaidClientConfig.INSTANCE.load();
		LiveMaidClientConfig.INSTANCE.save();

		AutoConfig.register(TalkBubblesConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(TalkBubblesConfig.class).getConfig();


		registerLiveMaidCommand();

		// 3rd person camera following processor register
		// 注册渲染事件监听器
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.player != null && getConfig().isThirdPersonFollow()) {
				LiveMaidThirdPersonCamera.updateCameraMinecraft3rdView(client.player);
			}
		});

//		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
//			IOUtilities.getLogger().info("Received Message: " + message);
//			String stringedMessage = message.toString();
//			if(
//					stringedMessage.contains("key='commands.message.display.incoming'")
//					&& stringedMessage.contains("literal{" + getConfig().getMasterName() + "}")
//			) {
//				String msgFromMaster = message.getString().replace(getConfig().getMasterName() + " whispers to you: ", "");
//				IOUtilities.getLogger().info("Received COMMAND FROM MASTER: " + msgFromMaster);
//				Master2MaidMessageProcessor.parseMessage(msgFromMaster);
//			}
//		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStopping);
	}





















	private void registerLiveMaidCommand() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("livemaid")
				.executes(context -> {
							context.getSource().sendFeedback(Text.literal("[Live Maid] 你好呀，主人~"));
							return 1;
						}
				)
				.then(ClientCommandManager.literal("reload")
						.executes(context -> {
							LiveMaidClientConfig.INSTANCE.load();
							LiveMaidClientConfig.INSTANCE.save();
							context.getSource().sendFeedback(Text.literal("[Live Maid] 已经重新加载配置了喵~"));
							return 1;
						})
				)
				.then(ClientCommandManager.literal("bind")
						.executes(context -> {
							context.getSource().sendFeedback(Text.literal("[Live Maid] 请选择一个直播平台绑定直播间捏~"));
							return 0;
						})
						.then(ClientCommandManager.literal("bilibilbi")
								.executes(context -> {
									context.getSource().sendFeedback(Text.literal("[Live Maid] 请您输入B站直播间的房间号捏~"));
									return 0;
								})
								.then(ClientCommandManager.argument("roomId", IntegerArgumentType.integer())
										.executes(context -> {
													Integer argRoomId = IntegerArgumentType.getInteger(context, "roomId");
													// 将roomId持久化到配置文件
													bindRoom(context.getSource(), argRoomId);
													return 1;
												}
										)
								)
						)
				)
				.then(ClientCommandManager.literal("cookie")
						.executes(context -> {
							context.getSource().sendFeedback(Text.literal("[Live Maid] 请选择一个平台绑定cookie捏~！"));
							return 0;
						})
						.then(ClientCommandManager.literal("bilibilbi")
								.executes(context -> {
									context.getSource().sendFeedback(Text.literal("[Live Maid] 请您输入B站直播间页面网页获取的cookie捏~ 您可以按F12之后控制台输入“document.cookie”来获得喵~"));
									return 0;
								})
								.then(ClientCommandManager.argument("bilibili cookie", StringArgumentType.greedyString())
										.executes(context -> {
													String cookie = StringArgumentType.getString(context, "bilibili cookie");
													// 将roomId持久化到配置文件
													getConfig().setBilibiliWebpageCookie(cookie);
													return 1;
												}
										)
								)
						)
				)
				.then(ClientCommandManager.literal("connect")
						.executes(context -> {
							if (this.biliBiliLiveRoom == null) {
								int roomid = getConfig().getBilibiliRoomId();
								if(roomid <= 0) {
									context.getSource().sendFeedback(Text.literal("[Live Maid] 请主人先绑定房间号捏~"));
									return 0;
								}
								bindRoom(context.getSource(), roomid);
							}

							this.biliBiliLiveRoom.startRoom();

							return 0;
						})
				)
				.then(ClientCommandManager.literal("set")
						.then(ClientCommandManager.literal("master")
								.executes(context -> {
									getConfig().setMasterName("");
									context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置主人大人的名字为空"));
									return 1;
								})
								.then(ClientCommandManager.argument("Master Name", StringArgumentType.greedyString())
										.executes(context -> {
											String name = StringArgumentType.getString(context, "Master Name");
											getConfig().setMasterName(name);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置主人大人的名字为：" + name));
											return 1;
										})
								)
						)
						.then(ClientCommandManager.literal("maid")
								.executes(context -> {
									getConfig().setMaidName("");
									context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置女仆的名字为空"));
									return 1;
								})
								.then(ClientCommandManager.argument("Maid Name", StringArgumentType.greedyString())
										.executes(context -> {
											String name = StringArgumentType.getString(context, "Maid Name");
											getConfig().setMaidName(name);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置女仆的名字为：" + name));
											return 1;
										})
								)
						)
				)
				.then(ClientCommandManager.literal("message")
						.then(ClientCommandManager.literal("sendDanmukuMsg")
								.then(ClientCommandManager.argument("sendDanmukuMsg T/F", BoolArgumentType.bool())
										.executes(context -> {
											var value = BoolArgumentType.getBool(context, "sendDanmukuMsg T/F");
											getConfig().setSendDanmukuMsg(value);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置发送直播弹幕消息选项为：" + value));
											return 1;
										})
								)
						)
						.then(ClientCommandManager.literal("sendGiftMsg")
								.then(ClientCommandManager.argument("sendGiftMsg T/F", BoolArgumentType.bool())
										.executes(context -> {
											var value = BoolArgumentType.getBool(context, "sendGiftMsg T/F");
											getConfig().setSendGiftMsg(value);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置发送礼物消息选项为：" + value));
											return 1;
										})
								)
						)
						.then(ClientCommandManager.literal("sendSuperChatMsg")
								.then(ClientCommandManager.argument("sendSuperChatMsg T/F", BoolArgumentType.bool())
										.executes(context -> {
											var value = BoolArgumentType.getBool(context, "sendSuperChatMsg T/F");
											getConfig().setSendSuperChatMsg(value);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置发送SC消息选项为：" + value));
											return 1;
										})
								)
						)
						.then(ClientCommandManager.literal("sendEnterRoomMsg")
								.then(ClientCommandManager.argument("sendEnterRoomMsg T/F", BoolArgumentType.bool())
										.executes(context -> {
											var value = BoolArgumentType.getBool(context, "sendEnterRoomMsg T/F");
											getConfig().setSendEnterRoomMsg(value);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置发送直播间进入消息选项为：" + value));
											return 1;
										})
								)
						)
						.then(ClientCommandManager.literal("sendLikeMsg")
								.then(ClientCommandManager.argument("sendLikeMsg T/F", BoolArgumentType.bool())
										.executes(context -> {
											var value = BoolArgumentType.getBool(context, "sendLikeMsg T/F");
											getConfig().setSendLikeMsg(value);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置发送点赞消息选项为：" + value));
											return 1;
										})
								)
						)
						.then(ClientCommandManager.literal("sendLiveStatusMsg")
								.then(ClientCommandManager.argument("sendLiveStatusMsg T/F", BoolArgumentType.bool())
										.executes(context -> {
											var value = BoolArgumentType.getBool(context, "sendLiveStatusMsg T/F");
											getConfig().setSendLiveStatusMsg(value);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置发送直播数据消息选项为：" + value));
											return 1;
										})
								)
						)
				)
				.then(ClientCommandManager.literal("disconnect")
						.executes(context -> {
							if(this.biliBiliLiveRoom != null) {
								this.biliBiliLiveRoom.stopRoom();
								context.getSource().sendFeedback(Text.literal("[Live Maid] 成功关闭和直播间的连接了！"));
							}
							return 1;
						})
				)
				.then(ClientCommandManager.literal("camera")
						.then(ClientCommandManager.literal("2or3-person")
								.then(ClientCommandManager.literal("avoid-block")
										.then(ClientCommandManager.argument("avoidBlock T/F", BoolArgumentType.bool())
												.executes(context -> {
													boolean value = BoolArgumentType.getBool(context, "avoidBlock T/F");
													getConfig().setThirdPersonNoBlock(value);
													context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置第三人称避免穿模为：" + value));
													return 1;
												})
										)
								)
								.then(ClientCommandManager.literal("position-lerp-ratio")
										.then(ClientCommandManager.argument("positionLerpRatio", DoubleArgumentType.doubleArg())
												.executes(context -> {
													double value = DoubleArgumentType.getDouble(context, "positionLerpRatio");
													getConfig().setPositionLerpRatio(value);
													context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置坐标平滑系数为：" + value));
													return 1;
												})
										)
								)
								.then(ClientCommandManager.literal("angle-lerp-ratio")
										.then(ClientCommandManager.argument("angleLerpRatio", DoubleArgumentType.doubleArg())
												.executes(context -> {
													double value = DoubleArgumentType.getDouble(context, "angleLerpRatio");
													getConfig().setAngleLerpRatio(value);
													context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置视角平滑系数为：" + value));
													return 1;
												})
										)
								)
								.then(ClientCommandManager.literal("offsets")
										.then(ClientCommandManager.literal("pitch")
												.then(ClientCommandManager.literal("set")
														.then(ClientCommandManager.argument("Pitch Offset", DoubleArgumentType.doubleArg())
																.executes(context -> {
																	double value = DoubleArgumentType.getDouble(context, "Pitch Offset");
																	getConfig().pitchDelta = value;
																	context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置视角水平偏移量为：" + value));
																	return 1;
																})
														)
												)
												.then(ClientCommandManager.literal("add")
														.then(ClientCommandManager.argument("Pitch Offset", DoubleArgumentType.doubleArg())
																.executes(context -> {
																	double value = DoubleArgumentType.getDouble(context, "Pitch Offset");
																	getConfig().pitchDelta += value;
																	context.getSource().sendFeedback(Text.literal("[Live Maid] 成功增加视角水平偏移量 " + value + "，现在的值是：" + getConfig().pitchDelta));
																	return 1;
																})
														)
												)
										)
										.then(ClientCommandManager.literal("yaw")
												.then(ClientCommandManager.literal("set")
														.then(ClientCommandManager.argument("Yaw Offset", DoubleArgumentType.doubleArg())
																.executes(context -> {
																	double value = DoubleArgumentType.getDouble(context, "Pitch Offset");
																	getConfig().yawDelta = value;
																	context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置视角水平偏移量为：" + value));
																	return 1;
																})
														)
												)
												.then(ClientCommandManager.literal("add")
														.then(ClientCommandManager.argument("Pitch Offset", DoubleArgumentType.doubleArg())
																.executes(context -> {
																	double value = DoubleArgumentType.getDouble(context, "Pitch Offset");
																	getConfig().yawDelta += value;
																	context.getSource().sendFeedback(Text.literal("[Live Maid] 成功增加视角水平偏移量 " + value + "，现在的值是：" + getConfig().yawDelta));
																	return 1;
																})
														)
												)
										)
										.then(ClientCommandManager.literal("radius")
												.then(ClientCommandManager.literal("set")
														.then(ClientCommandManager.argument("Yaw Offset", DoubleArgumentType.doubleArg())
																.executes(context -> {
																	double value = DoubleArgumentType.getDouble(context, "Yaw Offset");
																	getConfig().radiusDelta = value;
																	context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置视角与玩家距离偏移量为：" + value));
																	return 1;
																})
														)
												)
												.then(ClientCommandManager.literal("add")
														.then(ClientCommandManager.argument("Radius Offset", DoubleArgumentType.doubleArg())
																.executes(context -> {
																	double value = DoubleArgumentType.getDouble(context, "Radius Offset");
																	getConfig().radiusDelta += value;
																	context.getSource().sendFeedback(Text.literal("[Live Maid] 成功增加视角与玩家雨里偏移量 " + value + "，现在的值是：" + getConfig().radiusDelta));
																	return 1;
																})
														)
												)
										)
								)
						)
						.then(ClientCommandManager.literal("follow-mode")
								.then(ClientCommandManager.literal("3rd-person")
										.executes(context -> {
											getConfig().setThirdPersonFollow(true);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 成功设置人家的跟随模式为第三人称了捏~"));
											return 1;
										})
								)
								.then(ClientCommandManager.literal("none")
										.executes(context -> {
											getConfig().setThirdPersonFollow(false);
											context.getSource().sendFeedback(Text.literal("[Live Maid] 人家不会跟随主人了呢~ 下次主人需要的时候再叫我吧~"));
											return 1;
										})
								)
						)
				)
		));
	}

	// 客户端停止时的回调函数
	private void onClientStopping(MinecraftClient client) {
		// 执行清理资源的操作
		System.out.println("[Live Maid] 客户端正在被关闭，人家要清理使用过的资源了喵...");
		if(this.biliBiliLiveRoom != null) {
			this.biliBiliLiveRoom.stopRoom();
			System.out.println("[Live Maid] 成功关闭和直播间的连接了！");
		}
		System.out.println("[Live Maid] 资源已经全部清理好了喵 >_<");
		System.out.println("[Live Maid] 期待与主人下次相见呢~ 主人再见 0_0");
		// 在这里添加你的清理代码，例如关闭网络连接、保存数据等
	}

	private void bindRoom(FabricClientCommandSource source, Integer roomId) {
		this.biliBiliLiveRoom = new BiliBiliLiveRoom(roomId, source);
		source.sendFeedback(Text.literal("[Live Maid] 已绑定Bilibili直播间: " + roomId));
	}


	public static LiveMaidClientConfig getConfig() {
		return LiveMaidClientConfig.INSTANCE.instance();
	}
}