package com.kuahusg.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Utility;

/**
 * Created by kuahusg on 16-5-10.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long hours = 2 * 60 * 60 * 1000 + SystemClock.elapsedRealtime();
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, hours, pi);
        LogUtil.v(this.toString(), "Service onStartCommand!");

        return super.onStartCommand(intent, flags, startId);
    }


    private void updateWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String woeid = sharedPreferences.getString("woeid", "");
        Utility.queryWeather(woeid, AutoUpdateService.this, true);
    }
}
