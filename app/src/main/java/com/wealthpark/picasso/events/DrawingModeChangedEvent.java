package com.wealthpark.picasso.events;


import com.wealthpark.picasso.settings.PicassoSettings.DrawingMode;

/**
 * Created by sasa on 2/21/16.
 */
public class DrawingModeChangedEvent {
    private DrawingMode mNewDrawingMode;

    public DrawingModeChangedEvent(DrawingMode newDrawingMode) {
        mNewDrawingMode = newDrawingMode;
    }

    public DrawingMode getNewDrawingMode() {
        return mNewDrawingMode;
    }
}
