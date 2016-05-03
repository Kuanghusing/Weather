package com.kuahusg.weather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by kuahusg on 16-4-30.
 */
public class Myapplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}
