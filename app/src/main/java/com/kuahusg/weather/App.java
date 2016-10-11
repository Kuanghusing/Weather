package com.kuahusg.weather;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by kuahusg on 16-4-30.
 */
public class App extends Application {
    private static Context context;
    private RefWatcher watcher;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        watcher = LeakCanary.install(this);
    }

    public static Context getContext() {
        return context;
    }

    public static RefWatcher getWatcher(Context context) {
        App app = (App) context.getApplicationContext();
        return app.watcher;
    }
}
