package com.wealthpark.picasso.dagger.module;

import android.content.Context;

import com.wealthpark.picasso.analytics.PicassoAnalytics;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sasa on 2/21/16.
 */
@Module
public class AnalyticsModule {

    @Provides @Singleton
    PicassoAnalytics providesPicassoAnalytics(@Named(ApplicationModule.APPLICATION_CONTEXT) Context context) {
        return  new PicassoAnalytics(context);
    }
}
