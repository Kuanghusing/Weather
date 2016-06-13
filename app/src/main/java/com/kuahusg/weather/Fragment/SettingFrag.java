package com.kuahusg.weather.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import com.kuahusg.weather.R;
import com.kuahusg.weather.activities.About;
import com.kuahusg.weather.service.AutoUpdateService;
import com.kuahusg.weather.util.LogUtil;

/**
 * Created by kuahusg on 16-6-10.
 * com.kuahusg.weather.Fragment
 */
public class SettingFrag extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static final String AUTO_UPDATE = "auto_update";
    public static final String UPDATE_TIME = "update_time";
    public static final String OPEN_SOURCE = "open_source";
    public static final String ABOUT = "about";
    private SwitchPreference autoUpdatePreference;
    private EditTextPreference updateTimePreference;
    private Activity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        addPreferencesFromResource(R.xml.preference);


        autoUpdatePreference = (SwitchPreference) findPreference(AUTO_UPDATE);
        updateTimePreference = (EditTextPreference) findPreference(UPDATE_TIME);
        autoUpdatePreference.setOnPreferenceChangeListener(this);
        updateTimePreference.setOnPreferenceChangeListener(this);

        updateTimePreference.setEnabled(autoUpdatePreference.isChecked());
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case AUTO_UPDATE:
                updateTimePreference.setEnabled(autoUpdatePreference.isChecked());
                break;
            case ABOUT:
                Intent intent = new Intent(activity, About.class);
                activity.startActivity(intent);
                break;
            case OPEN_SOURCE:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://github.com/Kuanghusing/Weather"));
                activity.startActivity(intent1);
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Intent intent = new Intent(activity, AutoUpdateService.class);
        LogUtil.v(this.toString(), "onPreferenceChange()");
        if (preference.getKey().equals(SettingFrag.UPDATE_TIME)) {
            LogUtil.v(this.toString(), "onPreferenceChange() -> UPDATE");

            activity.stopService(intent);
            activity.startService(intent);
        } else if (preference.getKey().equals(SettingFrag.AUTO_UPDATE)) {
            LogUtil.v(this.toString(), "onPreferenceChange() -> AUTO");

            if ((boolean) newValue) {
                activity.startService(intent);
            } else {
                activity.stopService(intent);
            }
        }
        return true;
    }


}
