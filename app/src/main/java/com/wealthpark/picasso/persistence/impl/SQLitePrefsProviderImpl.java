package com.wealthpark.picasso.persistence.impl;

import android.content.Context;

import com.wealthpark.picasso.persistence.PersistenceProvider;

/**
 * Created by sasa on 2/21/16.
 */
public class SQLitePrefsProviderImpl implements PersistenceProvider {

    public SQLitePrefsProviderImpl(Context context) {

    }

    @Override
    public boolean isFirstStart() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setFirstStart(boolean firstStart) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
