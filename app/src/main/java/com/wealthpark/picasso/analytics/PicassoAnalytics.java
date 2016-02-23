package com.wealthpark.picasso.analytics;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wealthpark.picasso.R;

/**
 * Created by sasa on 2/21/16.
 */
public class PicassoAnalytics {
    public static final String TAG = "PicassoAnalytics";

    public static final String CATEGORY_ACTION = "action";

    public static final String ACTION_SHARE = "share";
    public static final String ACTION_CHANGE_COLOR = "change_color";
    public static final String ACTION_CLEAR_CANVAS = "clear_canvas";
    public static final String ACTION_CHANGE_BRUSH_MODE = "change_brush_mode";
    public static final String ACTION_RESIZE_BRUSH = "resize_brush";
    public static final String ACTION_DRAW_PATH = "draw_path";
    public static final String ACTION_UNDO = "undo";
    public static final String ACTION_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION= "request_ext_storage_permission";

    public static final String GA_OPT_IN = "GA_OPT_IN";

    private Tracker mTracker;
    private OnSharedPreferenceChangeListener mPrefChangeListener;

    public PicassoAnalytics(final Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        mTracker = analytics.newTracker(R.xml.global_tracker);

        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);

        final SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean optOut = !mSharedPrefs.getBoolean(GA_OPT_IN, false);
        GoogleAnalytics.getInstance(context).setAppOptOut(optOut);
        Log.d(TAG, String.format("GoogleAnalytics setAppOptOut(%s)", optOut));

        mPrefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(GA_OPT_IN)) {
                    boolean optOut = !mSharedPrefs.getBoolean(key, false);
                    GoogleAnalytics.getInstance(context).setAppOptOut(optOut);
                    Log.d(TAG, String.format("GoogleAnalytics setAppOptOut(%s)",optOut));
                }
            }
        };
        mSharedPrefs.registerOnSharedPreferenceChangeListener(mPrefChangeListener);
    }

    public void send(Map<String, String> event) {
        mTracker.send(event);
    }

    public void sendScreenView(String screenName) {
        if(screenName == null) return;

        mTracker.setScreenName(screenName);
        send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendAction(String action) {
        if(action == null) return;
        send(new HitBuilders.EventBuilder().setCategory(CATEGORY_ACTION).setAction(action).build());
    }

    public void sendAction(String action, String value) {
        if(action == null) return;
        send(new HitBuilders.EventBuilder().setCategory(CATEGORY_ACTION).setAction(action).setLabel(value).build());
    }
}
