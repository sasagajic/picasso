package com.wealthpark.picasso.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bowyer.app.fabtoolbar.FabToolbar;
import com.crashlytics.android.Crashlytics;
import com.flask.colorpicker.ColorPickerView.WHEEL_TYPE;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.squareup.otto.Subscribe;
import com.wealthpark.picasso.R;
import com.wealthpark.picasso.analytics.PicassoAnalytics;
import com.wealthpark.picasso.drawing.PicassoCanvas;
import com.wealthpark.picasso.drawing.PicassoCanvasView;
import com.wealthpark.picasso.events.BrushColorChangedEvent;
import com.wealthpark.picasso.events.BrushSizeChangedEvent;
import com.wealthpark.picasso.events.UndoAvailabilityChangeEvent;
import com.wealthpark.picasso.settings.PicassoSettings.DrawingMode;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends PicassoActivity {
    private static final String TAG = "MainActivity";
    private static final String TMP_FILE_NAME = "picasso_tmp";

    // ButterKnife bindings
    @Bind(R.id.mode_selection_fab) FloatingActionButton mModeSelectionButton;
    @Bind(R.id.undo_fab) FloatingActionButton mUndoButton;
    @Bind(R.id.floating_toolbar) FabToolbar mFloatingToolbar;
    @Bind(R.id.brush_size_seekBar) SeekBar mBrushSizeSeekBar;
    @Bind(R.id.brush_size_text) TextView mBrushSizeText;
    @Bind(R.id.ip_brush_color_hex) TextView mBrushColorHexText;
    @Bind(R.id.ip_color_sample) View mColorSampleView;
    @Bind(R.id.ip_brush_size_px) TextView mBIBrushSizeText;
    @Bind(R.id.brush_info_panel) CardView mBrushInfoPanel;

    @BindString(R.string.select_color) String mChooseColorString;
    @BindDimen(R.dimen.fab_margin) int mFabMargin;
    @BindDimen(R.dimen.fab_margin_off_screen) int mFabMarginOffScreen;

    private PicassoCanvas mPicassoCanvas;

    // Animations
    private AnimatorSet mBrushInfoPanelFlashAnimation;
    private AnimatorSet mUndoButtonSlideInAnimation;
    private AnimatorSet mUndoButtonSlideOutAnimation;
    private boolean mUndoButtonVisible = false;

    /********************************************************************************************
     *           Activity lifecycle methods
     *********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initAnimations();

        PicassoCanvasView picassoCanvasView = (PicassoCanvasView) findViewById(R.id.drawing_canvas);
        mPicassoCanvas = new PicassoCanvas(picassoCanvasView, getPicassoSettings(), getOttoBus(), getPicassoAnalytics());

        initBrushInfoPanel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getOttoBus().register(this);
        getOttoBus().register(mPicassoCanvas);

    }

    @Override
    protected void onResume() {
        super.onResume();
        flashBrushInfoPanel();
        if(mPicassoCanvas.isUndoAvailable()) {
            showUndoButton();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getOttoBus().unregister(this);
        getOttoBus().unregister(mPicassoCanvas);

        if(mFloatingToolbar.isFabExpanded()) {
            mFloatingToolbar.slideOutFab();
        }
    }

    @Override
    protected String getAnalyticsScreenName() {
        return TAG;
    }


    /********************************************************************************************
     *           Animations
     *********************************************************************************************/
    private void initAnimations() {
        mBrushInfoPanel.setAlpha(0.0f);
        mBrushInfoPanelFlashAnimation = initBrushInfoPanelFlashAnimation();
        mUndoButtonSlideInAnimation = initSlideInUndoButtonAnimation();
        mUndoButtonSlideOutAnimation = initSlideOutUndoButtonAnimation();
        mFloatingToolbar.setFab(mModeSelectionButton);
    }

    private AnimatorSet initSlideInUndoButtonAnimation() {
        ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(mUndoButton, "alpha", 0f, 1f);
        fadeInAnim.setDuration(500);

        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mUndoButton.getLayoutParams();
        ValueAnimator slideIn = ValueAnimator.ofInt(params.leftMargin, mFabMargin);
        slideIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.leftMargin = (Integer) valueAnimator.getAnimatedValue();
                mUndoButton.requestLayout();
            }
        });
        slideIn.setDuration(500);

        AnimatorSet slideInAnimatorSet = new AnimatorSet();
        slideInAnimatorSet.play(fadeInAnim).with(slideIn);
        return slideInAnimatorSet;
    }

    private AnimatorSet initSlideOutUndoButtonAnimation() {
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mUndoButton.getLayoutParams();
        ValueAnimator slideOut = ValueAnimator.ofInt(params.leftMargin, mFabMarginOffScreen);
        slideOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.leftMargin = (Integer) valueAnimator.getAnimatedValue();
                mUndoButton.requestLayout();
            }
        });
        slideOut.setDuration(500);

        AnimatorSet slideOutAnimatorSet = new AnimatorSet();
        slideOutAnimatorSet.play(slideOut);
        return slideOutAnimatorSet;
    }

    private void hideUndoButton() {
        mUndoButtonSlideOutAnimation.start();
        mUndoButtonVisible = false;
    }

    private void showUndoButton() {
        mUndoButtonSlideInAnimation.start();
        mUndoButtonVisible = true;
    }

    private void flashBrushInfoPanel() {
        if(!mBrushInfoPanelFlashAnimation.isRunning()) {
            mBrushInfoPanelFlashAnimation.start();
        }
    }

    private AnimatorSet initBrushInfoPanelFlashAnimation() {
        long startDelay = 500;
        long visibleDuration = TimeUnit.SECONDS.toMillis(5);

        ObjectAnimator fadeInAnim = ObjectAnimator.ofFloat(mBrushInfoPanel, "alpha", 0f, 1f);
        fadeInAnim.setDuration(500);
        ObjectAnimator fadeOutAnim = ObjectAnimator.ofFloat(mBrushInfoPanel, "alpha", 1f, 0f);
        fadeOutAnim.setDuration(500);

        AnimatorSet flashBrushInfoPanelAnimation = new AnimatorSet();
        flashBrushInfoPanelAnimation.play(fadeInAnim).after(startDelay);
        flashBrushInfoPanelAnimation.play(fadeOutAnim).after(fadeInAnim).after(visibleDuration);

        return flashBrushInfoPanelAnimation;
    }

    private void hideFloatingToolbar() {
        mFloatingToolbar.slideOutFab();
        if (mPicassoCanvas.isUndoAvailable()) {
            showUndoButton();
        }
    }

    /********************************************************************************************
     *           Main menu handling
     *********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_color:
                showColorPickerDialog();
                return true;

            case R.id.action_delete:
                clearCanvas();
                return true;

            case R.id.action_share:
                shareYourMasterpiece();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Displays color picker dialog to the user and propagates the selection to the app components.
     */
    private void showColorPickerDialog() {
        ColorPickerDialogBuilder.with(this).setTitle(mChooseColorString).initialColor(getPicassoSettings().getBrushColor())
            .wheelType(WHEEL_TYPE.FLOWER).density(12).setPositiveButton(getString(R.string.ok), new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                getPicassoSettings().setBrushColor(selectedColor);
                getPicassoAnalytics().sendAction(PicassoAnalytics.ACTION_CHANGE_COLOR, String.valueOf("#" + Integer.toHexString
                    (selectedColor)));
            }
        }).build().show();
        getPicassoAnalytics().sendScreenView("ColorPickerDialog");
    }

    /**
     * Presents the confirmation dialog to the user and clears the drawing if operation is confirmed
     */
    private void clearCanvas() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_drawing).setMessage(R.string.confirm_delete).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPicassoCanvas.clear();
                getPicassoAnalytics().sendAction(PicassoAnalytics.ACTION_CLEAR_CANVAS);
            }

        }).setNegativeButton(R.string.no, null).show();
        getPicassoAnalytics().sendScreenView("ConfirmDeleteDialog");
    }

    /**
     * Launches implicit intent to share the drawing. It saves the bitmap to the external temp folder in order for other apps
     * to have access to the image.
     */
    private void shareYourMasterpiece() {
        File file = new File(getCacheDir(), TMP_FILE_NAME + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            mPicassoCanvas.asBitmap().compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(intent);

            getPicassoAnalytics().sendAction(PicassoAnalytics.ACTION_SHARE);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

    }


    /********************************************************************************************
     *           OnClick handlers
     *********************************************************************************************/
    @OnClick(R.id.mode_selection_fab)
    public void showDrawingModeToolbar() {
        mFloatingToolbar.expandFab();

        if(mBrushInfoPanelFlashAnimation.isRunning()){
            mBrushInfoPanelFlashAnimation.cancel();
            mBrushInfoPanel.setAlpha(0f);
        }

        hideUndoButton();
    }

    @OnClick(R.id.select_brush_mode_btn)
    public void setBrushMode() {
        setImageForModeSelectionButton(R.drawable.ic_brush_white_36dp);
        getPicassoSettings().setBrushMode(DrawingMode.BRUSH);
        updateColorSampleTo(getPicassoSettings().getBrushColor());
        hideFloatingToolbar();
        getPicassoAnalytics().sendAction(PicassoAnalytics.ACTION_CHANGE_BRUSH_MODE, "BRUSH");
    }

    @OnClick(R.id.select_eraser_mode_bnt)
    public void setEraserMode() {
        setImageForModeSelectionButton(R.drawable.ic_erase_white_36dp);
        getPicassoSettings().setBrushMode(DrawingMode.ERASER);
        updateColorSampleTo(getPicassoSettings().getCanvasColor());
        hideFloatingToolbar();
        getPicassoAnalytics().sendAction(PicassoAnalytics.ACTION_CHANGE_BRUSH_MODE, "ERASER");
    }

    @OnClick(R.id.undo_fab)
    public void undo() {
        mPicassoCanvas.undo();
        getPicassoAnalytics().sendAction(PicassoAnalytics.ACTION_UNDO);
    }


    /********************************************************************************************
     *           Event bus handlers
     *********************************************************************************************/
    @Subscribe
    public void onUndoAvailabilityEvent(UndoAvailabilityChangeEvent event){
        if(event.isAvailable()){
            if(!mFloatingToolbar.isFabExpanded() && !mUndoButtonVisible){
                showUndoButton();
            }
        } else {
            if(mUndoButtonVisible) {
                hideUndoButton();
            }
        }
    }

    @Subscribe
    public void onBrushColorChangedEvent(BrushColorChangedEvent event) {
        updateColorSampleTo(event.getNewColor());
    }

    @Subscribe
    public void onBrushSizeChangedEvent(BrushSizeChangedEvent event) {
        updateBrushSizeTo(event.getNewSize());
    }


    /********************************************************************************************
     *           UI updates
     *********************************************************************************************/
    private void initBrushInfoPanel() {
        updateColorSampleTo(getPicassoSettings().getBrushColor());

        final int brushSize = getPicassoSettings().getBrushSize();
        mBrushSizeText.setText(brushSize + " px");
        mBIBrushSizeText.setText(brushSize + " px");
        mBrushSizeSeekBar.setProgress(brushSize);
        mBrushSizeSeekBar.setMax(getPicassoSettings().getMaxBrushSize());

        mBrushSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int minBrushSize = getPicassoSettings().getMinBrushSize();
            int maxBrushSize = getPicassoSettings().getMaxBrushSize();
            int brushSize = getPicassoSettings().getBrushSize();
            int progress = minBrushSize + brushSize;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = (progresValue + minBrushSize <= maxBrushSize) ? progresValue + minBrushSize : maxBrushSize;
                mBrushSizeText.setText(progress + " px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mBrushSizeText.setText(progress + " px");
                getPicassoSettings().setBrushSize(progress);
                hideFloatingToolbar();
                getPicassoAnalytics().sendAction(PicassoAnalytics.ACTION_RESIZE_BRUSH, String.valueOf(progress));
            }
        });
    }

    private void updateBrushSizeTo(int size) {
        mBIBrushSizeText.setText(size + " px");
        flashBrushInfoPanel();
    }

    private void updateColorSampleTo(int color) {
        GradientDrawable sd = (GradientDrawable) mColorSampleView.getBackground();
        sd.setColor(color);
        mBrushColorHexText.setText("#" + Integer.toHexString(color));

        flashBrushInfoPanel();
    }

    private void setImageForModeSelectionButton(int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mModeSelectionButton.setImageDrawable(getResources().getDrawable(drawableId, getTheme()));
        } else {
            mModeSelectionButton.setImageDrawable(getResources().getDrawable(drawableId));
        }
    }

}
