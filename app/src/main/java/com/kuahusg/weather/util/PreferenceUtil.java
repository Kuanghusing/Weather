package com.kuahusg.weather.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kuahusg.weather.App;

/**
 * Created by kuahusg on 16-9-27.
 */

public class PreferenceUtil {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String PREF_HAS_LOAD_ALL_CITY = "hasLoadAllCity";
    public static final String PREF_SELECTED_CITY = "selectCity";
    public static final String PREF_WOEID = "woeid";
    public static final String PREF_CITY_SIMPLE_NAME = "selectCitySimpleName";

    private PreferenceUtil() {

    }

    public static PreferenceUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }


    public SharedPreferences getSharedPreferences() {
        // TODO: 16-9-27 context from application work??
        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return sharedPreferences;
    }

    public SharedPreferences.Editor getSharedPreferencesEditor() {
        if (editor == null) {
            editor = getSharedPreferences().edit();
        }

        return editor;
    }


    private static final class InstanceHolder {
        static final PreferenceUtil INSTANCE = new PreferenceUtil();
    }
}
