package com.kuahusg.weather.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.kuahusg.weather.Fragment.SettingFrag;

/**
 * Created by kuahusg on 16-6-10.
 * com.kuahusg.weather.activities
 */
public class Setting extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preference);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFrag()).commit();
    }
}
