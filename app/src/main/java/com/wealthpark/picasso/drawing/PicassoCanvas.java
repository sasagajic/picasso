package com.wealthpark.picasso.drawing;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.view.MotionEvent;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.wealthpark.picasso.analytics.PicassoAnalytics;
import com.wealthpark.picasso.events.BrushColorChangedEvent;
import com.wealthpark.picasso.events.BrushSizeChangedEvent;
import com.wealthpark.picasso.events.DrawingModeChangedEvent;
import com.wealthpark.picasso.events.UndoAvailabilityChangeEvent;
import com.wealthpark.picasso.settings.PicassoSettings;
import com.wealthpark.picasso.settings.PicassoSettings.DrawingMode;

/**
 * Created by sasa on 2/19/16.
 */
public class PicassoCanvas implements PicassoCanvasView.InteractionListener {
    // Dependencies
    private PicassoCanvasView mCanvas;
    private PicassoSettings mSettings;
    private Bus mOttoBus;
    private PicassoAnalytics mAnalytics;

    // Internals
    private ArrayList<PicassoPath> mPaths;
    private PicassoPath mCurrentPath;
    private Canvas mBufferCanvas;
    private Bitmap mBufferBitmap;
    private boolean mShouldClearCanvas = false;
    private boolean mUndoAvailable = false;
    private boolean mEnabled = true;

    public PicassoCanvas(PicassoCanvasView canvas, PicassoSettings settings, Bus ottoBus, PicassoAnalytics analytics) {
        mCanvas = canvas;
        mCanvas.addInteractionListener(this);
        mSettings = settings;
        mOttoBus = ottoBus;
        mAnalytics = analytics;

        mPaths = new ArrayList<PicassoPath>();
        mCurrentPath = new PicassoPath(mSettings.getBrushColor(), mSettings.getBrushSize(), DrawingMode.BRUSH);
    }

    public void clear() {
        mPaths.clear();
        mShouldClearCanvas = true;
        mCanvas.invalidate();
        updateUndoAvailability();
    }

    public void undo() {
        if(mPaths.size() > 0) {
            PicassoPath pathToRemove = mPaths.get(mPaths.size() - 1);
            if(pathToRemove.getMode() == DrawingMode.ERASER) {
                mCurrentPath.setBrushColor(mSettings.getCanvasColor());
            } else {
                mCurrentPath.setBrushColor(mSettings.getBrushColor());
            }
            mPaths.remove(mPaths.size() - 1);
        }
        updateUndoAvailability();
        mCanvas.invalidate();
    }

    private void updateUndoAvailability() {
        if(mPaths.size() > 0) {
            mUndoAvailable = true;
        } else {
            mUndoAvailable = false;
        }
        mOttoBus.post(new UndoAvailabilityChangeEvent(mUndoAvailable));
    }

    @Override
    public void onCanvasSizeChanged(int w, int h, int oldw, int oldh) {
        mBufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

    @Override
    public void onCanvasDraw(Canvas canvas) {
        if(mShouldClearCanvas) {
            mBufferCanvas.drawColor(mSettings.getCanvasColor(), PorterDuff.Mode.CLEAR);
            mShouldClearCanvas = false;
        }

        for (PicassoPath path: mPaths) {
            path.drawOn(canvas);
        }
        mCurrentPath.drawOn(canvas);
    }

    @Override
    public void onTouchEvent(float touchX, float touchY, int action) {
        if(!mEnabled) { return; }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrentPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mPaths.add(mCurrentPath);
                mCurrentPath = new PicassoPath(mSettings.getBrushColor(), mSettings.getBrushSize(), mCurrentPath.getMode());
                updateUndoAvailability();
                mAnalytics.sendAction(PicassoAnalytics.ACTION_DRAW_PATH);
                break;
            default:
                return;
        }
    }

    public Bitmap asBitmap() {
        return mBufferBitmap;
    }

    public boolean isUndoAvailable() {
        return mUndoAvailable;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Subscribe
    public void onBrushColorChangedEvent(BrushColorChangedEvent event) {
        mCurrentPath.setBrushColor(event.getNewColor());
    }

    @Subscribe
    public void onBrushSizeChangedEvent(BrushSizeChangedEvent event) {
        mCurrentPath.setBrushSize(event.getNewSize());
    }

    @Subscribe
    public void onBrushModeChangedEvent(DrawingModeChangedEvent event) {
        DrawingMode mode = event.getNewDrawingMode();
        switch (mode) {
            case BRUSH:
                mCurrentPath.setBrushColor(mSettings.getBrushColor());
                mCurrentPath.setMode(DrawingMode.BRUSH);
                break;
            case ERASER:
                mCurrentPath.setBrushColor(mSettings.getCanvasColor());
                mCurrentPath.setMode(DrawingMode.ERASER);
                break;
            default:
        }
    }

    class PicassoPath extends Path {
        private Paint mPaint;
        private DrawingMode mMode;

        public PicassoPath(int color, int size, DrawingMode mode) {
            super();
            mMode = mode;
            mPaint = new Paint();
            mPaint.setColor(color);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(size);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Cap.ROUND);
        }

        public void setBrushSize(int newSize) {
            mPaint.setStrokeWidth(newSize);
        }

        public void setBrushColor(int newColor) {
            mPaint.setColor(newColor);
        }

        public void drawOn(Canvas canvas) {
            canvas.drawPath(this, mPaint);
        }

        public DrawingMode getMode() {
            return mMode;
        }

        public void setMode(DrawingMode mode) {
            this.mMode = mode;
        }
    }

}