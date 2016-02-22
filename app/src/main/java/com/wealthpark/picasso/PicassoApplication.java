package com.wealthpark.picasso;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.wealthpark.picasso.dagger.ApplicationComponent;
import com.wealthpark.picasso.dagger.DaggerApplicationComponent;
import com.wealthpark.picasso.dagger.module.ApplicationModule;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by sasa on 2/19/16.
 */
public class PicassoApplication extends Application {
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Build up the dependency injection object graph
        mApplicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();

        // Setup support for custom fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/MysteryQuest-Regular.ttf")
            .setFontAttrId(R.attr.fontPath).build());

        // Fabric init
        Fabric.with(this, new Crashlytics());
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
