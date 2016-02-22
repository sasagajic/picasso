package com.wealthpark.picasso.persistence.impl;

import android.content.SharedPreferences;

import com.wealthpark.picasso.persistence.PersistenceProvider;

/**
 * Created by sasa on 2/21/16.
 */
public class SharedPrefsProviderImpl implements PersistenceProvider {

    private static final String KEY_BRUSH_SIZE = "brush_size";
    private static final String KEY_BRUSH_COLOR = "brush_color";






    private final SharedPreferences mSharedPrefs;

    public SharedPrefsProviderImpl(SharedPreferences prefs) {
        mSharedPrefs = prefs;
    }

}
