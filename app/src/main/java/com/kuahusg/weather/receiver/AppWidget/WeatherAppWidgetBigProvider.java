package com.kuahusg.weather.receiver.AppWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.activities.WeatherActivity;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.WeatherUtil;

import java.util.List;

/**
 * Created by kuahusg on 16-6-17.
 * com.kuahusg.weather.receiver
 */
public class WeatherAppWidgetBigProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LogUtil.v(this.toString(), "onUpdate()");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedCity = sharedPreferences.getString("selectCitySimpleName", "N");
        String woeid = sharedPreferences.getString("woeid", "");
        /*
         weather information
         */
        List<Forecast> forecastList = WeatherUtil.loadForecastFromDatabase(woeid);
        Forecast forecast_to_show = null;
        if (forecastList != null && forecastList.size() > 0) {
            forecast_to_show = forecastList.get(0);
        } else {
            return;
        }
        String tempAndDate;
        ForecastInfo info = WeatherUtil.loadForecastInfoFromDatabase(woeid);
        String temp_now = "NaN";
        String date = "NaN";

        if (info != null) {
/*            String[] t = tempAndDate.split("\\|");
            temp_now = t[0];
            date = t[1].substring(17, 25);*/
            temp_now = info.getTemp();
            date = info.getDate().substring(17,25);
        }



        /*
         update view
         */
        for (int appwidgetId :
                appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_weather_big);
            rv.setImageViewResource(R.id.weather_pic, getWeatherPicId(forecast_to_show.getText()));
            rv.setTextViewText(R.id.city_name, selectedCity);
            rv.setTextViewText(R.id.weather_info, forecast_to_show.getText());
            rv.setTextViewText(R.id.date, date);
            rv.setTextViewText(R.id.temp_now, temp_now);
            rv.setTextViewText(R.id.temp, forecast_to_show.getLow() + "|" + forecast_to_show.getHigh());

            Intent intent = new Intent(context, WeatherActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            rv.setOnClickPendingIntent(R.id.main_container, pendingIntent);

            appWidgetManager.updateAppWidget(appwidgetId, rv);
        }
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
