package com.kuahusg.weather.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.service.AutoUpdateService;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Utility;

import java.util.List;

/**
 * Created by kuahusg on 16-4-28.
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private static TextView title;
    private static TextView date;
    private static TextView temp_now;
    private static TextView temp1;
    private static TextView temp2;
    private static TextView weather_text;
    private static City selectCity;
    private static String tempAndPushDate;
    private static List<Forecast> forecastList;
    private static Toolbar toolbar;
    public static RelativeLayout weather_info;
    public static ProgressDialog progressDialog;

    private static ImageView weahterPic;
    public static Context mcontext;
    public static CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        mcontext = getApplicationContext();
//        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.public_data);
        temp_now = (TextView) findViewById(R.id.temp_now);
        temp1 = (TextView) findViewById(R.id.temp1);
        temp2 = (TextView) findViewById(R.id.temp2);
        weather_text = (TextView) findViewById(R.id.weather_text);
        weather_info = (RelativeLayout) findViewById(R.id.weather_info);
        weahterPic = (ImageView) findViewById(R.id.weather_pic);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("loading...");
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setOnMenuItemClickListener(this);


//        title.setText(R.string.loading);
        date.setVisibility(View.INVISIBLE);
        weather_info.setVisibility(View.INVISIBLE);

//        title.setOnClickListener(this);
        weather_info.setOnClickListener(this);
//        progressDialog = new ProgressDialog(this);

        selectCity = (City) getIntent().getSerializableExtra("selectCity");
        boolean anotherCity = getIntent().getBooleanExtra("anotherCity", false);
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
        if (anotherCity) {
            queryWeatherFromServer(selectCity);
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
        Utility.queryWeather(selectCity.getWoeid(), mcontext, false);

    }


    private static void showWeather() {
        if (!forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(0);
            String weatherText = forecastToday.getWeatherText();
            temp1.setText(forecastToday.getLow());
            temp2.setText(forecastToday.getHigh());
            weather_text.setText(weatherText);
//            title.setText(selectCity.getCity_name());
            toolbar.setSubtitle(selectCity.getCity_name());

            date.setVisibility(View.VISIBLE);
            weather_info.setVisibility(View.VISIBLE);
            showWeatherPic(weatherText);

            Intent intent = new Intent(mcontext, AutoUpdateService.class);
            mcontext.startService(intent);


        } else {
            LogUtil.v(mcontext.toString(), "no size in forecastList");
        }
//        dismissProgress();
    }

    private static void showTempAndDate() {
        if (!TextUtils.isEmpty(tempAndPushDate)) {

            String[] t = tempAndPushDate.split("\\|");
            temp_now.setText(t[0]);
//            String date = t[1].replace(" CST", "").replaceAll(" \\d{4}", "");
            String date = t[1].substring(17, 25);
            WeatherActivity.date.setText(date);
        }
    }

    private static void showWeatherPic(String weatherText) {
        if (weatherText.contains("Thunderstorms")) {
            weahterPic.setImageResource(R.drawable.weather_thunderstorm);

        } else if (weatherText.contains("Cloudy")) {
            weahterPic.setImageResource(R.drawable.weather_cloudy);
        } else if (weatherText.contains("Sunny")) {
            weahterPic.setImageResource(R.drawable.weather_sun_day);

        } else if (weatherText.contains("Showers") || weatherText.contains("Rain")) {
            weahterPic.setImageResource(R.drawable.weather_rain);
        }

    }


    private ProgressDialog showProgress() {
        if (!this.isFinishing()) {
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(WeatherActivity.this);

            }
            LogUtil.v(this.toString(), WeatherActivity.this.toString());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setTitle("");
            progressDialog.show();
        }


//        progressDialog.show();
        return progressDialog;
    }

    public static void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_info:
                alertDialog(this.getString(R.string.sure), this.getString(R.string.refresh), this.getString(R.string.no), this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        showProgress();
                        queryWeatherFromServer(selectCity);
//                        title.setText(R.string.loading);
                        toolbar.setSubtitle(R.string.loading);
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
//                    dismissProgress();
                    break;
                case SHOW_WEATHER:
                    forecastList = WeatherDB.loadForecast();
                    showWeather();
                    Snackbar.make(weather_info, mcontext.getString(R.string.load_finish), Snackbar.LENGTH_LONG).show();
                    break;
                case SHOW_TEMP_DATE:
                    tempAndPushDate = WeatherDB.loadTempAndDate();
                    showTempAndDate();
                    break;

            }
        }
    };

/*    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.v(this.toString(), "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(this.toString(), "onResume()");
        LogUtil.v(this.toString() + "\tisFinishing?", this.isFinishing() + "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.v(this.toString(), "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.v(this.toString(), "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
        LogUtil.v(this.toString(), "OnDestroy()");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                break;
            case R.id.refresh_btn:
                alertDialog(this.getString(R.string.sure), this.getString(R.string.refresh), this.getString(R.string.no), this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queryWeatherFromServer(selectCity);
                        toolbar.setSubtitle(R.string.loading);
                    }
                });
                break;
            case R.id.change_btn:

                alertDialog(this.getString(R.string.sure), this.getString(R.string.switch_city),
                        this.getString(R.string.no), this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(WeatherActivity.this, SelectArea.class);
                                intent.putExtra("isFromWeatherActivity", true);
                                startActivity(intent);
                                finish();
                            }
                        });

        }
        return false;
    }
}
