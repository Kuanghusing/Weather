package com.kuahusg.weather.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Myapplication;
import com.kuahusg.weather.util.Utility;

import java.util.List;

/**
 * Created by kuahusg on 16-4-28.
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private static TextView title;
    private static TextView data;
    private static TextView temp_now;
    private static TextView temp1;
    private static TextView temp2;
    private static TextView weather_text;
    private static City selectCity;
    private static String tempAndPushDate;
    private static List<Forecast> forecastList;
    private RelativeLayout weather_info;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        title = (TextView) findViewById(R.id.title);
        data = (TextView) findViewById(R.id.public_data);
        temp_now = (TextView) findViewById(R.id.temp_now);
        temp1 = (TextView) findViewById(R.id.temp1);
        temp2 = (TextView) findViewById(R.id.temp2);
        weather_text = (TextView) findViewById(R.id.weather_text);
        weather_info = (RelativeLayout) findViewById(R.id.weather_info);

        title.setOnClickListener(this);
        weather_info.setOnClickListener(this);
//        progressDialog = new ProgressDialog(this);

        selectCity = (City) getIntent().getSerializableExtra("selectCity");
        if (selectCity != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("selectCity", selectCity.getFullNmae());
            editor.putString("woeid", selectCity.getWoeid());
            editor.putString("city_name", selectCity.getCity_name());
            editor.apply();

        } else {
            LogUtil.v(this.getClass().getName(), "selectCity is null!");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String fullName = preferences.getString("selectCity", "");
            String woeid = preferences.getString("woeid", "");
            String city_name = preferences.getString("city_name", "");
            LogUtil.v(this.getClass().getName(), fullName + "\t" + woeid);
            City city = new City(city_name, woeid, fullName);
            selectCity = city;
        }
        tempAndPushDate = WeatherDB.loadTempAndDate();
        forecastList = WeatherDB.loadForecast();
        if (forecastList.size() <= 0 || TextUtils.isEmpty(tempAndPushDate)) {
            queryWeatherFromServer(selectCity);
        } else {
            showWeather();
            showTempAndDate();
        }


    }

    private void queryWeatherFromServer(final City selectCity) {
        showProgress();
        Utility.queryWeather(selectCity.getWoeid());


//        ProgressDialog progressDialog = showProgress();
//        dismissProgress(progressDialog);

    }

/*    @Override
    protected void onDestroy() {
        super.onDestroy();
    }*/

/*    @Override
    public void onBackPressed() {
        super.onDestroy();
//        super.onBackPressed();
    }*/

    private static void showWeather() {
//        progressDialog = ProgressDialog.show(this, "loading","title");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        showProgress();
        if (!forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(0);
            temp1.setText(forecastToday.getLow() + "℃");
            temp2.setText(forecastToday.getHigh() + "℃");
            weather_text.setText(forecastToday.getWeatherText());
            title.setText(selectCity.getCity_name());

        } else {
            LogUtil.v(Myapplication.getContext().getClass().getName(), "no size in forecastList");
        }
//        dismissProgress();
    }

    private static void showTempAndDate() {
        if (!TextUtils.isEmpty(tempAndPushDate)) {

            String[] t = tempAndPushDate.split("\\|");
            temp_now.setText(t[0] + "℃");
            String date = t[1].replace(" CST", "").replaceAll(" \\d{4}", "");
            data.setText(date);
        }
    }

    private ProgressDialog showProgress() {
        if (progressDialog == null) {

            progressDialog = new ProgressDialog(WeatherActivity.this);
        }
        progressDialog.setMessage("loading");
        progressDialog.setTitle("title");
        progressDialog.show();


//        progressDialog.show();
        return progressDialog;
    }

    private static void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title:
                alertDialog("?", "switch the city?", "NO!!", "Yes~", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WeatherActivity.this, SelectArea.class);
                        intent.putExtra("isFromWeatherActivity", true);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.id.weather_info:
                alertDialog("Sure?", "refesh the weather info?", "NO!NO!NO!", "Sure!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgress();
                        queryWeatherFromServer(selectCity);
                    }
                });
                break;
        }
    }

    private void alertDialog(String title, String message, String negativeString, String positiveString, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(positiveString, listener)
                .show();


    }

    public static final int SHOW_WEATHER = 2;
    public static final int PROSSDIALOG_DISSMISS = 1;
    public static final int SHOW_TEMP_DATE = 3;
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROSSDIALOG_DISSMISS:
                    dismissProgress();
                    break;
                case SHOW_WEATHER:
                    forecastList = WeatherDB.loadForecast();
                    showWeather();
                    break;
                case SHOW_TEMP_DATE:
                    showTempAndDate();
                    break;

            }
        }
    };
}
