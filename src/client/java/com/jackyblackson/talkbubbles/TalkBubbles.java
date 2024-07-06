package com.jackyblackson.talkbubbles;

import com.jackyblackson.talkbubbles.config.TalkBubblesConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;



public class TalkBubbles implements ClientModInitializer {

    public static TalkBubblesConfig CONFIG = new TalkBubblesConfig();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(TalkBubblesConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TalkBubblesConfig.class).getConfig();
    }

}
