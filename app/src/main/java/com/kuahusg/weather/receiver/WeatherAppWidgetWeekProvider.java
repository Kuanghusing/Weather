package com.kuahusg.weather.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.Forecast;

import java.util.List;

/**
 * Created by kuahusg on 16-6-18.
 * com.kuahusg.weather.receiver
 */
public class WeatherAppWidgetWeekProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String cityName = sharedPreferences.getString("selectCitySimpleName", "N");
        /*
         get the data from database
         */
        List<Forecast> forecastList = WeatherDB.loadForecast();
        String tempAndDate = WeatherDB.loadTempAndDate();
        String temp_now = "N";
        String date = "N";
        Forecast forecast = null;
        if (forecastList == null || forecastList.size() == 0) {
            return;
        }
        if (!TextUtils.isEmpty(tempAndDate)) {
            String[] s = tempAndDate.split("\\|");
            temp_now = s[0];
            date = s[1];
        }

        for (int appwidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.weather_appwidget_week);
            forecast = forecastList.get(0);
            rv.setImageViewResource(R.id.weather_pic, getWeatherPicId(forecast.getWeatherText()));
            rv.setTextViewText(R.id.city_name, cityName);
            rv.setTextViewText(R.id.date, date.substring(17, 25));
            rv.setTextViewText(R.id.temp_now, temp_now);
            rv.setTextViewText(R.id.temp, forecast.getLow() + "|" + forecast.getHigh());
            rv.setTextViewText(R.id.weather_info, forecastList.get(0).getWeatherText());

            forecast = forecastList.get(1);
            rv.setImageViewResource(R.id.first_pic, getWeatherPicId(forecast.getWeatherText()));
            rv.setTextViewText(R.id.first_date, forecast.getDate());
            rv.setTextViewText(R.id.first_temp, forecast.getLow() + "|" + forecast.getHigh());

            forecast = forecastList.get(2);
            rv.setImageViewResource(R.id.second_pic, getWeatherPicId(forecast.getWeatherText()));
            rv.setTextViewText(R.id.second_date, forecast.getDate());
            rv.setTextViewText(R.id.third_temp, forecast.getLow() + "|" + forecast.getHigh());

            forecast = forecastList.get(3);
            rv.setImageViewResource(R.id.third_pic, getWeatherPicId(forecast.getWeatherText()));
            rv.setTextViewText(R.id.date, forecast.getDate().substring(17, 25));
            rv.setTextViewText(R.id.temp, forecast.getLow() + "|" + forecast.getHigh());

            forecast = forecastList.get(4);
            rv.setImageViewResource(R.id.fourth_pic, getWeatherPicId(forecast.getWeatherText()));
            rv.setTextViewText(R.id.fourth_date, forecast.getDate().substring(17, 25));
            rv.setTextViewText(R.id.temp, forecast.getLow() + "|" + forecast.getHigh());

            appWidgetManager.updateAppWidget(appwidgetId, rv);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    private int getWeatherPicId(String weatherText) {
        if (weatherText.contains("Thunderstorms")) {
            return R.drawable.weather_thunderstorm;

        } else if (weatherText.contains("Cloudy")) {
            return R.drawable.weather_cloudy;
        } else if (weatherText.contains("Sunny")) {
            return R.drawable.weather_sun_day;

        } else if (weatherText.contains("Showers") || weatherText.contains("Rain")) {
            return R.drawable.weather_rain;
        } else if (weatherText.contains("Breezy")) {
            return R.drawable.weather_wind;
        }
        return 0;


    }


}
