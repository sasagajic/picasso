package com.wealthpark.picasso.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.wealthpark.picasso.R;

/**
 * Created by sasa on 2/22/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }

}
