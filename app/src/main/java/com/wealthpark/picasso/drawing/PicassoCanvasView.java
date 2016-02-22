package com.wealthpark.picasso.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sasa on 2/18/16.
 */
public class PicassoCanvasView extends View {
    private InteractionListener mListener;

    public PicassoCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addInteractionListener(InteractionListener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mListener != null) {
            mListener.onCanvasSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mListener != null) {
            mListener.onCanvasDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mListener != null) {
            mListener.onTouchEvent(event.getX(), event.getY(), event.getAction());
        }
        invalidate();
        return true;
    }

    public interface InteractionListener {
        void onCanvasSizeChanged(int w, int h, int oldw, int oldh);

        void onCanvasDraw(Canvas canvas);

        void onTouchEvent(float touchX, float touchY, int action);
    }
}
