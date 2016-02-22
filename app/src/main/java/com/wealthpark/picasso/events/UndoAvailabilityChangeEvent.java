package com.wealthpark.picasso.events;

/**
 * Created by sasa on 2/22/16.
 */
public class UndoAvailabilityChangeEvent {
    private boolean mAvailable;

    public UndoAvailabilityChangeEvent(boolean available) {
        mAvailable = available;
    }

    public boolean isAvailable() {
        return mAvailable;
    }
}
