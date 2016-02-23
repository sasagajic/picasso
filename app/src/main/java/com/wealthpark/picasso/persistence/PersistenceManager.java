package com.wealthpark.picasso.persistence;

/**
 * Created by sasa on 2/21/16.
 */
public class PersistenceManager {
    private final PersistenceProvider mPersistenceProvider;

    public PersistenceManager(PersistenceProvider provider) {
        mPersistenceProvider = provider;
    }

    public boolean isFirstStart() {
        return mPersistenceProvider.isFirstStart();
    }

    public void setFirstStart(boolean firstStart) {
        mPersistenceProvider.setFirstStart(firstStart);
    }

}
