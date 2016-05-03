package com.kuahusg.weather.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Myapplication;
import com.kuahusg.weather.util.Utility;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by kuahusg on 16-4-28.
 */
public class WeatherActivity extends AppCompatActivity {
    private TextView title;
    private TextView data;
    private TextView temp_now;
    private TextView temp1;
    private TextView temp2;
    private TextView weather_text;
    private City selectCity;
    private String tempAndPushDate;
    private static List<Forecast> forecastList;
    private ProgressDialog progressDialog;

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
        }


    }

    private void queryWeatherFromServer(final City selectCity) {
        showProgress();
        boolean querySuccessfully = Utility.queryWeather(selectCity.getWoeid());
        dismissProgress();

        if (querySuccessfully) {
            forecastList = WeatherDB.loadForecast();
            tempAndPushDate = WeatherDB.loadTempAndDate();
            showWeather();


        }

    }

/*    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();
    }*/

    private void showWeather() {
//        progressDialog = ProgressDialog.show(this, "loading", null,true,true);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        showProgress();
        if (!forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(0);
            String[] t = tempAndPushDate.split("\\|");
            temp_now.setText(t[0] + "℃");
            String date = t[1].replace(" CST", "").replaceAll(" \\d{4}", "");
            data.setText(date);
            temp1.setText(forecastToday.getLow() + "℃");
            temp2.setText(forecastToday.getHigh() + "℃");
            weather_text.setText(forecastToday.getWeatherText());
            title.setText(selectCity.getCity_name());

        } else {
            LogUtil.v(this.getClass().getName(), "no size in forecastList");
        }
        dismissProgress();
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, "loading", null, true, true);
        }

        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();
        }
    }
}
