package com.gDyejeekis.aliencompanion.Fragments.SettingsFragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.gDyejeekis.aliencompanion.R;

/**
 * Created by George on 9/10/2016.
 */
public class NavigationSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navigation_preferences);
    }
}