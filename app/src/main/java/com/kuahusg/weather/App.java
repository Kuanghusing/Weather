package com.kuahusg.weather;

import android.app.Application;
import android.content.Context;

/**
 * Created by kuahusg on 16-4-30.
 */
public class App extends Application {
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
