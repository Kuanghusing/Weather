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
import com.kuahusg.weather.util.PreferenceUtil;

import java.util.List;

/**
 * Created by kuahusg on 16-6-18.
 * com.kuahusg.weather.receiver
 */
public class WeatherAppWidgetWeekProvider extends BaseAppWidget {
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        final String cityName = PreferenceUtil.getCitySimpleName();

        getDatasource().queryWeather(null, new RequestWeatherCallback() {
            @Override
            public void success(List<Forecast> forecasts, ForecastInfo forecastInfo) {
                String temp_now = "N";
                String date = "N";
                Forecast forecast = null;
                if (forecasts == null || forecastInfo == null)
                    return;
                forecast = forecasts.get(0);
                temp_now = forecastInfo.getTemp();
                date = forecastInfo.getDate();

                for (int appwidgetId : appWidgetIds) {
                    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_weather_week);
                    rv.setImageViewResource(R.id.weather_pic, getWeatherPicId(forecast.getText()));
                    rv.setTextViewText(R.id.city_name, cityName);
                    rv.setTextViewText(R.id.date, date.substring(0, 6));
                    rv.setTextViewText(R.id.temp_now, temp_now);
                    rv.setTextViewText(R.id.temp, forecast.getLow() + "|" + forecast.getHigh());
                    rv.setTextViewText(R.id.weather_info, forecasts.get(0).getText());

                    forecast = forecasts.get(1);
                    rv.setImageViewResource(R.id.first_pic, getWeatherPicId(forecast.getText()));
                    rv.setTextViewText(R.id.first_date, forecast.getDate().substring(0, 6));
                    rv.setTextViewText(R.id.first_temp, forecast.getLow() + "|" + forecast.getHigh());

                    forecast = forecasts.get(2);
                    rv.setImageViewResource(R.id.second_pic, getWeatherPicId(forecast.getText()));
                    rv.setTextViewText(R.id.second_date, forecast.getDate().substring(0, 6));
                    rv.setTextViewText(R.id.second_temp, forecast.getLow() + "|" + forecast.getHigh());

                    forecast = forecasts.get(3);
                    rv.setImageViewResource(R.id.third_pic, getWeatherPicId(forecast.getText()));
                    rv.setTextViewText(R.id.third_date, forecast.getDate().substring(0, 6));
                    rv.setTextViewText(R.id.third_temp, forecast.getLow() + "|" + forecast.getHigh());

                    forecast = forecasts.get(4);
                    rv.setImageViewResource(R.id.fourth_pic, getWeatherPicId(forecast.getText()));
                    rv.setTextViewText(R.id.fourth_date, forecast.getDate().substring(0, 6));
                    rv.setTextViewText(R.id.fourth_temp, forecast.getLow() + "|" + forecast.getHigh());

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
