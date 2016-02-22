package com.wealthpark.picasso.settings;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.squareup.otto.Bus;
import com.wealthpark.picasso.events.BrushColorChangedEvent;
import com.wealthpark.picasso.events.BrushSizeChangedEvent;
import com.wealthpark.picasso.events.DrawingModeChangedEvent;

/**
 * Created by sasa on 2/21/16.
 */
public class PicassoSettings {
    public enum DrawingMode {BRUSH, ERASER}

    private static final int BRUSH_SIZE_MIN_DP = 10;
    private static final int BRUSH_SIZE_MAX_DP = 54;
    private static final int BRUSH_SIZE_DEFAULT_DP = 24;
    private static final String BRUSH_COLOR_DEFAULT = "#942e2eff";

    private Bus mOttoBus;
    private int mBrushSize;
    private int mMinBrushSize;
    private int mMaxBrushSize;
    private int mBrushColor;
    private DrawingMode mBrushMode;
    private int mCanvasColor;


    public PicassoSettings(Context context, Bus ottoBus) {
        mOttoBus = ottoBus;

        mBrushSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BRUSH_SIZE_DEFAULT_DP, context.getResources()
            .getDisplayMetrics());
        mMinBrushSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BRUSH_SIZE_MIN_DP, context.getResources()
            .getDisplayMetrics());
        mMaxBrushSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BRUSH_SIZE_MAX_DP, context.getResources()
            .getDisplayMetrics());
        mBrushColor = Color.parseColor(BRUSH_COLOR_DEFAULT);
        mBrushMode = DrawingMode.BRUSH;

        mCanvasColor = Color.WHITE;
    }

    public int getBrushSize() {
        return mBrushSize;
    }

    public void setBrushSize(int brushSize) {
        mBrushSize = brushSize;
        mOttoBus.post(new BrushSizeChangedEvent(brushSize));
    }

    public int getBrushColor() {
        return mBrushColor;
    }

    public void setBrushColor(int brushColor) {
        mBrushColor = brushColor;
        mOttoBus.post(new BrushColorChangedEvent(brushColor));
    }

    public DrawingMode getBrushMode() {
        return mBrushMode;
    }

    public void setBrushMode(DrawingMode brushMode) {
        mBrushMode = brushMode;
        mOttoBus.post(new DrawingModeChangedEvent(brushMode));
    }

    public int getCanvasColor() {
        return mCanvasColor;
    }

    public int getMinBrushSize() {
        return mMinBrushSize;
    }

    public int getMaxBrushSize() {
        return mMaxBrushSize;
    }
}
