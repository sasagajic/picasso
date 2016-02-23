package com.wealthpark.picasso.persistence.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wealthpark.picasso.persistence.PersistenceProvider;

/**
 * Created by sasa on 2/21/16.
 */
public class SharedPrefsProviderImpl implements PersistenceProvider {
    private static final String KEY_FIRST_START = "first_start";

    private final SharedPreferences mSharedPrefs;

    public SharedPrefsProviderImpl(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public boolean isFirstStart() {
        boolean firstStart = mSharedPrefs.getBoolean(KEY_FIRST_START, true);
        if(firstStart) {
            setFirstStart(false);
        }
        return firstStart;
    }

    @Override
    public void setFirstStart(boolean firstStart) {
        mSharedPrefs.edit().putBoolean(KEY_FIRST_START, firstStart).apply();
    }
}
