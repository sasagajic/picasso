package com.wealthpark.picasso.events;

/**
 * Created by sasa on 2/21/16.
 */
public class BrushSizeChangedEvent {
    private int mSize;

    public BrushSizeChangedEvent(int newSize) {
        mSize = newSize;
    }

    public int getNewSize() {
        return mSize;
    }
}
