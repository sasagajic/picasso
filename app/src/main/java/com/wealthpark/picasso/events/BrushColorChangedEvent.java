package com.wealthpark.picasso.events;

/**
 * Created by sasa on 2/21/16.
 */
public class BrushColorChangedEvent {
    private int mColor;

    public BrushColorChangedEvent(int newColor) {
        mColor = newColor;
    }

    public int getNewColor() {
        return mColor;
    }
}
