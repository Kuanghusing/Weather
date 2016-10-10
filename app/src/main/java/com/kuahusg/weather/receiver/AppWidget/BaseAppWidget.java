package com.kuahusg.weather.receiver.AppWidget;

import android.appwidget.AppWidgetProvider;

import com.kuahusg.weather.data.IDataSource;
import com.kuahusg.weather.data.WeatherDataSource;
import com.kuahusg.weather.data.local.LocalForecastDataSource;
import com.kuahusg.weather.data.remote.RemoteForecastDataSource;

import java.lang.ref.WeakReference;

/**
 * Created by kuahusg on 16-10-10.
 */

public abstract class BaseAppWidget extends AppWidgetProvider {
    protected WeakReference<IDataSource> dataSource;

    protected IDataSource getDatasource() {
        if (dataSource == null) {
            dataSource = new WeakReference<IDataSource>(new WeatherDataSource(new RemoteForecastDataSource(), new LocalForecastDataSource()));

        }
        return dataSource.get();
    }

}
