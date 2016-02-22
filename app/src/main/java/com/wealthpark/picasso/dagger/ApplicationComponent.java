package com.wealthpark.picasso.dagger;

import com.wealthpark.picasso.activity.PicassoActivity;
import com.wealthpark.picasso.dagger.module.AnalyticsModule;
import com.wealthpark.picasso.dagger.module.ApplicationModule;
import com.wealthpark.picasso.dagger.module.PersistenceModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sasa on 2/21/16.
 */
@Singleton
@Component(modules = {ApplicationModule.class, PersistenceModule.class, AnalyticsModule.class})
public interface ApplicationComponent {
    void inject(PicassoActivity activity);
}
