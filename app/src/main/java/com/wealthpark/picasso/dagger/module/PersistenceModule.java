package com.wealthpark.picasso.dagger.module;

import android.content.Context;

import com.wealthpark.picasso.persistence.PersistenceManager;
import com.wealthpark.picasso.persistence.impl.SQLitePrefsProviderImpl;
import com.wealthpark.picasso.persistence.impl.SharedPrefsProviderImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sasa on 2/21/16.
 */
@Module
public class PersistenceModule {
    public static final String SHARED_PREFS_PERSISTENCE_MANAGER = "SharedPrefsPersistenceManager";
    public static final String SQLITE_PERSISTENCE_MANAGER = "SQLitePersistenceManager";

    @Provides @Singleton @Named(SHARED_PREFS_PERSISTENCE_MANAGER)
    PersistenceManager provideSharedPrefsPersistenceManager(@Named(ApplicationModule.APPLICATION_CONTEXT) Context context) {
        return new PersistenceManager(new SharedPrefsProviderImpl(context));
    }

    /**
     * This is not used anywhere in the app at the moment. I might implement that SQLite provider if there is time. In any
     * case, just wanted to demonstrate how easy would be to switch the implementation of the persistence manager - even in
     * runtime.
     */
    @Provides @Singleton @Named(SQLITE_PERSISTENCE_MANAGER)
    PersistenceManager provideSQLitePersistenceManager(@Named(ApplicationModule.APPLICATION_CONTEXT) Context context) {
        return new PersistenceManager(new SQLitePrefsProviderImpl(context));
    }

}
