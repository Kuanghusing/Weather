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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.service.AutoUpdateService;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Utility;

import java.util.ArrayList;
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
    private static TextView cal_date;
    private static City selectCity;
    private static String tempAndPushDate;
    private static DrawerLayout drawerLayout;
    private static NavigationView navigationView;
    private ActionBar actionBar;
    public static FloatingActionButton fab;
    private static List<Forecast> forecastList;
    private static Toolbar toolbar;
    public static RelativeLayout weather_info;
    private static LinearLayout public_layout;
    public static ProgressDialog progressDialog;

    private static ImageView weahterPic;
    public static Context mcontext;

    public static RelativeLayout weather_more_info;

    private static int whichDay = 0;

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
        weather_more_info = (RelativeLayout) findViewById(R.id.main_info);
        public_layout = (LinearLayout) findViewById(R.id.public_layout);
        cal_date = (TextView) findViewById(R.id.date_textview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(this);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("loading...");
        toolbar.setOnMenuItemClickListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_container);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        date.setVisibility(View.INVISIBLE);
        weather_info.setVisibility(View.INVISIBLE);
        weather_more_info.setVisibility(View.INVISIBLE);
        public_layout.setVisibility(View.INVISIBLE);
        weather_info.setOnClickListener(this);

        InitWeather();

        Intent intent = new Intent(mcontext, AutoUpdateService.class);
        mcontext.startService(intent);


    }

    private void queryWeatherFromServer(final City selectCity) {
        Utility.queryWeather(selectCity.getWoeid(), mcontext, false);

    }

    private void InitWeather() {
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
            showWeather(whichDay);
            showTempAndDate();
            setupDrawerContent(navigationView);

        }
    }


    private static void showWeather(int whichDay) {
        if (!forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(whichDay);
            String weatherText = forecastToday.getWeatherText();
            temp1.setText(forecastToday.getLow());
            temp2.setText(forecastToday.getHigh());
            weather_text.setText(weatherText);
            toolbar.setSubtitle(selectCity.getCity_name());
            cal_date.setText(forecastList.get(whichDay).getDate().substring(0, 6));

            date.setVisibility(View.VISIBLE);
            weather_info.setVisibility(View.VISIBLE);
            weather_more_info.setVisibility(View.VISIBLE);
            public_layout.setVisibility(View.VISIBLE);
            showWeatherPic(weatherText);


        } else {
            LogUtil.v(mcontext.toString(), "no size in forecastList");
        }
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
        } else if (weatherText.contains("Breezy")) {
            weahterPic.setImageResource(R.drawable.weather_wind);
        }

    }


/*    private ProgressDialog showProgress() {
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
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_info:
                queryWeatherFromServer(selectCity);
                toolbar.setSubtitle(R.string.loading);
                break;
            case R.id.fab:
                queryWeatherFromServer(selectCity);
                toolbar.setSubtitle(R.string.loading);
                break;
        }
    }


    private void alertDialog(String title, String message, String negativeString, String positiveString,
                             DialogInterface.OnClickListener listener) {
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


    private static void setupDrawerContent(NavigationView navigationView) {
        String date;
        List<Integer> idList = new ArrayList<>();
        idList.add(R.id.day1);
        idList.add(R.id.day2);
        idList.add(R.id.day3);
        idList.add(R.id.day4);
        idList.add(R.id.day5);
        for (int i = 2; i < 5; i++) {
            date = forecastList.get(i).getDate().substring(0, 6);
            navigationView.getMenu().findItem(idList.get(i)).setTitle(date);

        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.day1:
                        whichDay = 0;
                        break;
                    case R.id.day2:
                        whichDay = 1;
                        break;
                    case R.id.day3:
                        whichDay = 2;
                        break;
                    case R.id.day4:
                        whichDay = 3;
                        break;
                    case R.id.day5:
                        whichDay = 4;
                        break;
                    case R.id.about:
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent i = new Intent(mcontext, About.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mcontext.startActivity(i);
                            }
                        }, 250);
                        break;

                }


                showWeather(whichDay);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                    drawerLayout.closeDrawers();
                }

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_setting:
                Toast.makeText(this, "还没完成...", Toast.LENGTH_LONG).show();
                break;
            /*case R.id.refresh_btn:
                alertDialog(this.getString(R.string.sure), this.getString(R.string.refresh), this.getString(R.string.no), this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queryWeatherFromServer(selectCity);
                        toolbar.setSubtitle(R.string.loading);
                    }
                });
                break;*/
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
                break;

        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                    break;
                case SHOW_WEATHER:
                    forecastList = WeatherDB.loadForecast();
                    showWeather(whichDay);

                    if (navigationView != null) {
                        setupDrawerContent(navigationView);
                    }
                    Snackbar.make(fab, mcontext.getString(R.string.load_finish), Snackbar.LENGTH_LONG)
                            .setAction(mcontext.getString(R.string.yes), null)
                            .show();
                    break;
                case SHOW_TEMP_DATE:
                    tempAndPushDate = WeatherDB.loadTempAndDate();
                    showTempAndDate();
                    break;

            }
        }
    };
}
