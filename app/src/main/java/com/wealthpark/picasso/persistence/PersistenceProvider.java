package com.wealthpark.picasso.persistence;

/**
 * Created by sasa on 2/21/16.
 */
public interface PersistenceProvider {
    boolean isFirstStart();
    void setFirstStart(boolean firstStart);
}
