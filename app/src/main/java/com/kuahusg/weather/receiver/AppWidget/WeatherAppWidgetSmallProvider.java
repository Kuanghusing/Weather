package com.kuahusg.weather.receiver.AppWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.activities.rebuild.WeatherMainActivity;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-6-18.
 * com.kuahusg.weather.receiver
 */
public class WeatherAppWidgetSmallProvider extends BaseAppWidget {
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {


        getDatasource().queryWeather(null, new RequestWeatherCallback() {
            @Override
            public void success(List<Forecast> forecasts, ForecastInfo forecastInfo) {
                String temp_now = "NaN";
                if (forecastInfo == null || forecasts == null)
                    return;
                temp_now = forecastInfo.getTemp();
                Forecast forecast_to_show = null;
                forecast_to_show = forecasts.get(0);

                for (int appwidgetId :
                        appWidgetIds) {
                    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_weather_small);
                    rv.setImageViewResource(R.id.weather_pic, getWeatherPicId(forecast_to_show.getText()));
                    rv.setTextViewText(R.id.weather_info, forecast_to_show.getText());
                    rv.setTextViewText(R.id.temp_now, temp_now);

                    Intent intent = new Intent(context, WeatherMainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    rv.setOnClickPendingIntent(R.id.main_container, pendingIntent);
                    appWidgetManager.updateAppWidget(appwidgetId, rv);
                }
            }

            @Override
            public void error(String message) {

            }
        });

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private int getWeatherPicId(String weatherText) {
        if (weatherText.contains("Thunderstorms")) {
            return R.drawable.thunderstorm;

        } else if (weatherText.contains("Cloudy")) {
            return R.drawable.cloudy;
        } else if (weatherText.contains("Sunny")) {
            return R.drawable.sunny;

        } else if (weatherText.contains("Showers") || weatherText.contains("Rain")) {
            return R.drawable.rain3;
        } else if (weatherText.contains("Breezy")) {
            return R.drawable.wind;
        } else if (weatherText.contains("snow")) {
            return R.drawable.snow2;
        } else {
            return R.drawable.sun;
        }


    }
}
