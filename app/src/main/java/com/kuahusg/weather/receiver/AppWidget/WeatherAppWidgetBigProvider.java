package com.kuahusg.weather.receiver.AppWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.activities.WeatherMainActivity;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.bean.Forecast;
import com.kuahusg.weather.model.bean.ForecastInfo;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.PreferenceUtil;

import java.util.List;

import static com.kuahusg.weather.util.PreferenceUtil.PREF_CITY_SIMPLE_NAME;

/**
 * Created by kuahusg on 16-6-17.
 * com.kuahusg.weather.receiver
 */
public class WeatherAppWidgetBigProvider extends BaseAppWidget {
    private Forecast forecast_to_show;
    private String temp_now = "NaN";
    private String date = "NaN";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(this.getClass().getSimpleName(), "onEnabled");
        // TODO: 16-10-8 should check auto update
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        LogUtil.v(this.toString(), "onUpdate()");


        getDatasource().queryWeather(null, new RequestWeatherCallback() {
            @Override
            public void success(List<Forecast> forecasts, ForecastInfo forecastInfo) {
                if (forecasts != null && forecasts.size() > 0) {
                    forecast_to_show = forecasts.get(0);
                } else {
                    return;
                }
                if (forecastInfo != null) {
                    temp_now = forecastInfo.getTemp();
                    date = forecastInfo.getDate().substring(17, 25);
                }
                for (int appwidgetId :
                        appWidgetIds) {
                    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_weather_big);
                    rv.setImageViewResource(R.id.weather_pic, getWeatherPicId(forecast_to_show.getText()));
                    rv.setTextViewText(R.id.city_name, PreferenceUtil.getInstance().getSharedPreferences().getString(PREF_CITY_SIMPLE_NAME, "N"));
                    rv.setTextViewText(R.id.weather_info, forecast_to_show.getText());
                    rv.setTextViewText(R.id.date, date);
                    rv.setTextViewText(R.id.temp_now, temp_now);
                    rv.setTextViewText(R.id.temp, forecast_to_show.getLow() + "|" + forecast_to_show.getHigh());

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
