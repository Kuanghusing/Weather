package com.kuahusg.weather.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kuahusg.weather.R;
import com.kuahusg.weather.model.Data.WeatherResult;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.model.db.WeatherDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-27.
 */
public class WeatherUtil {

    private static Context mContext;
    private static WeatherDB db;
    private static UpdateWeatherCallback updateWeatherCallback;
    public static final int ERROR = 1;
    private static Message message;


    public synchronized static boolean queryWeather(String woeid, final Context context, UpdateWeatherCallback updateWeatherCallback) {
        db = WeatherDB.getInstance(context);
        String address = "https://query.yahooapis.com/v1/public/yql?q="
                + "select * from weather.forecast "
                + "where woeid = " + woeid + " and u=\"c\"&format=json";

        WeatherUtil.updateWeatherCallback = updateWeatherCallback;


        WeatherDB.getInstance(context);

        WeatherDB.deleteTable("info");
        WeatherDB.deleteTable("Forecast");
        WeatherUtil.mContext = context;


        new UpdateForecastTask(woeid).execute(address.replaceAll(" ", "%20").replaceAll("\"", "%22"));

        return true;

    }


    public static ForecastInfo loadForecastInfoFromDatabase(String woeid) {

        return WeatherDB.loadForecastInfo(woeid);
    }


    public static List<Forecast> loadForecastFromDatabase(String woeid) {
        return WeatherDB.loadForecast(woeid);
    }


    private static class UpdateForecastTask extends AsyncTask<String, String, WeatherResult> {

        String woeid;

        public UpdateForecastTask(String woeid) {
            this.woeid = woeid;
        }

        @Override
        protected WeatherResult doInBackground(String... params) {


            String result;
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");
            } catch (Exception e) {
                e.printStackTrace();
//                updateWeatherCallback.error(mContext.getString(R.string.no_network));

                message = new Message();
                message.what = ERROR;
                handler.sendMessage(message);

                return null;
            }


            Gson gson = new Gson();
            WeatherResult weatherResult = null;
            try {
                weatherResult = gson.fromJson(result, WeatherResult.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                updateWeatherCallback.error(mContext.getString(R.string.no_result));
                cancel(true);
            }


            LogUtil.v(this.getClass().toString(), "solve weather info finish");

            return weatherResult;
        }


        @Override
        protected void onPostExecute(WeatherResult weatherResult) {
            super.onPostExecute(weatherResult);

            List<Forecast> forecastList = new ArrayList<>();
            ForecastInfo forecastInfo = null;
            if (weatherResult == null) {
                return;
            }


            Forecast[] forecasts = new Forecast[0];
            try {
                forecasts = weatherResult.getQuery().getResults().getChannel().getItem().getForecast();
                forecasts = weatherResult.getQuery().getResults().getChannel().getItem().getForecast();
                String link = weatherResult.getQuery().getResults().getChannel().getLink();
                String lastBuildDate = weatherResult.getQuery().getResults().getChannel()
                        .getLastBuildDate();
                String wind_direction = weatherResult.getQuery().getResults().getChannel()
                        .getWind().getDirection();
                String wind_speed = weatherResult.getQuery().getResults().getChannel().getWind()
                        .getSpeed();
                String date = weatherResult.getQuery().getResults().getChannel().getItem()
                        .getCondition().getDate();
                String temp = weatherResult.getQuery().getResults().getChannel().getItem()
                        .getCondition().getTemp();
                String text = weatherResult.getQuery().getResults().getChannel().getItem()
                        .getCondition().getText();
                String sunrise = weatherResult.getQuery().getResults().getChannel().getAstronomy()
                        .getSunrise();
                String sunset = weatherResult.getQuery().getResults().getChannel().getAstronomy()
                        .getSunset();

                forecastInfo = new ForecastInfo(link, lastBuildDate, wind_direction, wind_speed, date,
                        temp, text, woeid, sunrise, sunset);


            } catch (Exception e) {
                e.printStackTrace();
                updateWeatherCallback.error(mContext.getString(R.string.no_result));
            }

            LogUtil.v(this.toString(), "length:\t" + forecasts.length + "");

            /**
             * save to database
             */
            if (forecasts.length > 0) {
                for (Forecast f :
                        forecasts) {
                    f.setWoeid(woeid);
                    forecastList.add(f);
                    WeatherDB.saveForecast(f);

                }
            }
            WeatherDB.saveForecastInfo(forecastInfo);


            /**
             * callback
             */
            updateWeatherCallback.updateWeather(forecastList);
            updateWeatherCallback.updateWeatherInfo(forecastInfo);

        }


    }


    public interface UpdateWeatherCallback {
        void updateWeather(List<Forecast> forecastList);

        void updateWeatherInfo(ForecastInfo forecastInfo);


        void error(String message);
    }

    /*public interface ShowResultCallback{
        void showResult(String message);
    }*/

    public interface GetWeatherCallback {
        void getWeather(List<Forecast> forecastList);

        void getWeatherInfo(ForecastInfo info);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ERROR:
                    updateWeatherCallback.error(mContext.getString(R.string.no_network));
                    break;
            }
        }
    };

}
