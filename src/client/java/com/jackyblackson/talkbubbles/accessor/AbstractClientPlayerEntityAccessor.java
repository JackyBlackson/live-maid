package com.jackyblackson.talkbubbles.accessor;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public interface AbstractClientPlayerEntityAccessor {

    public void live_maid$setChatText(List<String> text, int currentAge, int width, int height);

    @Nullable
    public List<String> live_maid$getChatText();

    public int live_maid$getOldAge();

    public int live_maid$getWidth();

    public int live_maid$getHeight();
}
