package com.kuahusg.weather.UI.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.widget.Toast;

import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.activities.rebuild.AboutMeActivity;
import com.kuahusg.weather.service.AutoUpdateService;
import com.kuahusg.weather.util.LogUtil;

/**
 * Created by kuahusg on 16-6-10.
 * com.kuahusg.weather.UI.Fragment
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static final String AUTO_UPDATE = "auto_update";
    public static final String UPDATE_TIME = "update_time";
    public static final String OPEN_SOURCE = "open_source";
    public static final String ABOUT = "about";
    public static final String UPDATE_APP = "update_app";
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
                Intent intent = new Intent(activity, AboutMeActivity.class);
                activity.startActivity(intent);
                break;
            case OPEN_SOURCE:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse("https://github.com/Kuanghusing/Weather"));
                activity.startActivity(intent1);
                break;
            case UPDATE_APP:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://fir.im/eync"));
                startActivity(i);
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Intent intent = new Intent(activity, AutoUpdateService.class);
        LogUtil.v(this.toString(), "onPreferenceChange()");
        if (preference.getKey().equals(SettingFragment.UPDATE_TIME)) {
            LogUtil.v(this.toString(), "onPreferenceChange() -> UPDATE");
            if (TextUtils.isEmpty((String) newValue)) {
                Toast.makeText(activity, activity.getString(R.string.no_value_error), Toast.LENGTH_LONG).show();
                return false;
            }
            activity.stopService(intent);
            activity.startService(intent);
        } else if (preference.getKey().equals(SettingFragment.AUTO_UPDATE)) {
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
