package com.jackyblackson.mixin.client.talkbubbles;

import java.util.List;

import com.jackyblackson.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin implements AbstractClientPlayerEntityAccessor {

    @Nullable
    private List<String> chatTextList = null;
    private int oldAge = 0;
    private int width;
    private int height;

    @Override
    public void live_maid$setChatText(List<String> textList, int currentAge, int width, int height) {
        this.chatTextList = textList;
        this.oldAge = currentAge;
        this.width = width;
        this.height = height;
    }

    @Nullable
    @Override
    public List<String> live_maid$getChatText() {
        return this.chatTextList;
    }

    @Override
    public int live_maid$getOldAge() {
        return this.oldAge;
    }

    @Override
    public int live_maid$getWidth() {
        return this.width;
    }

    @Override
    public int live_maid$getHeight() {
        return this.height;
    }
}
