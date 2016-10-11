package com.kuahusg.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.kuahusg.weather.UI.Fragment.SettingFragment;
import com.kuahusg.weather.data.IDataSource;
import com.kuahusg.weather.data.WeatherDataSource;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.data.local.LocalForecastDataSource;
import com.kuahusg.weather.data.remote.RemoteForecastDataSource;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.receiver.AutoUpdateReceiver;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.PreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by kuahusg on 16-5-10.
 */
public class AutoUpdateService extends Service {
    private double time = 2;

    //    private IDataSource dataSource = new WeatherDataSource(new RemoteForecastDataSource(), new LocalForecastDataSource());
    private WeakReference<IDataSource> dataSourceWeakReference = new WeakReference<IDataSource>(new WeatherDataSource(new RemoteForecastDataSource(), new LocalForecastDataSource()));


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.v(this.toString(), "Service onCreate()");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        time = Double.valueOf(PreferenceUtil.getInstance().getSharedPreferences().getString(SettingFragment.UPDATE_TIME, "0"));
        if (time <= 0)
            time = 2;
        // TODO: 16-10-8 ??
        dataSourceWeakReference.get().queryWeather(null, new RequestWeatherCallback() {
            @Override
            public void success(List<Forecast> forecasts, ForecastInfo forecastInfo) {
            }

            @Override
            public void error(String message) {

            }
        });

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long hours = (long) (time * 60 * 60 * 1000 + SystemClock.elapsedRealtime());
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, hours, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.v(this.toString(), "Service onDestroy()");
    }


}
