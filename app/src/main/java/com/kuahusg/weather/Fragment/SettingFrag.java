package com.kuahusg.weather.Fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import com.kuahusg.weather.R;

/**
 * Created by kuahusg on 16-6-10.
 * com.kuahusg.weather.Fragment
 */
public class SettingFrag extends PreferenceFragment {
    public static final String AUTO_UPDATE = "auto_update";
    public static final String UPDATE_TIME = "update_time";
    private SwitchPreference autoUpdatePreference;
    private EditTextPreference updateTimePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        autoUpdatePreference = (SwitchPreference) findPreference(AUTO_UPDATE);
        updateTimePreference = (EditTextPreference) findPreference(UPDATE_TIME);

        updateTimePreference.setEnabled(autoUpdatePreference.isChecked());
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "auto_update":
                updateTimePreference.setEnabled(autoUpdatePreference.isChecked());
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
