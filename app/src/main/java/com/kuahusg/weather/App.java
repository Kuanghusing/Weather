package com.kuahusg.weather;

import android.app.Application;
import android.content.Context;

import com.kuahusg.weather.util.CrashHandler;

import java.util.IllegalFormatException;

/**
 * Created by kuahusg on 16-4-30.
 */
public class App extends Application {
    private static Context context;
//    private RefWatcher watcher;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init();

/*        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        watcher = LeakCanary.install(this);*/
    }

    public static Context getContext() {
        return context;
    }

/*    public static RefWatcher getWatcher(Context context) {
        App app = (App) context.getApplicationContext();
        return app.watcher;
    }*/
}
