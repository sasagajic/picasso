package com.wealthpark.picasso.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Bus;
import com.wealthpark.picasso.PicassoApplication;
import com.wealthpark.picasso.analytics.PicassoAnalytics;
import com.wealthpark.picasso.dagger.ApplicationComponent;
import com.wealthpark.picasso.settings.PicassoSettings;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by sasa on 2/21/16.
 */
public abstract class PicassoActivity extends AppCompatActivity {
    @Inject PicassoAnalytics mPicassoAnalytics;
    @Inject Bus mOttoBus;
    @Inject PicassoSettings mPicassoSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationComponent appComponent = ((PicassoApplication) getApplication()).getApplicationComponent();
        appComponent.inject(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPicassoAnalytics.sendScreenView(getAnalyticsScreenName());
    }

    protected abstract String getAnalyticsScreenName();

    public PicassoAnalytics getPicassoAnalytics() {
        return mPicassoAnalytics;
    }

    public Bus getOttoBus() {
        return mOttoBus;
    }

    public PicassoSettings getPicassoSettings() {
        return mPicassoSettings;
    }
}
