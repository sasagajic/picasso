package com.wealthpark.picasso.dagger.module;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.wealthpark.picasso.settings.PicassoSettings;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sasa on 2/21/16.
 */
@Module
public class ApplicationModule {
    public static final String APPLICATION_CONTEXT = "ApplicationContext";

    private final Context mContext;

    public ApplicationModule(Context context) {
        mContext = context;
    }

    @Provides @Singleton @Named(APPLICATION_CONTEXT)
    Context provideApplicationContext() {
        return  mContext;
    }

    @Provides @Singleton
    Bus provideOttoBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Provides @Singleton
    PicassoSettings providePicassoSettings(@Named(APPLICATION_CONTEXT) Context context, Bus bus) {
        return new PicassoSettings(context, bus);
    }

}
