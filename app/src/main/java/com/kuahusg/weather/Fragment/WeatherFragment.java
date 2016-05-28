package com.kuahusg.weather.Fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuahusg.weather.R;
import com.kuahusg.weather.activities.WeatherActivity;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;

import java.util.List;

/**
 * Created by kuahusg on 16-5-25.
 */

public class WeatherFragment extends Fragment implements View.OnClickListener {
    private View view;
    private static List<Forecast> forecastList;
    private City selectCity;
    private String tempAndPushDate;
    private Context mContext;

    private TextView date;
    private TextView temp_now;
    private TextView temp1;
    private TextView temp2;
    private TextView weather_text;
    private TextView cal_date;
    private ImageView weatherPic;
    private RelativeLayout weather_info;
    private LinearLayout public_layout;
    private RelativeLayout weather_more_info;

    private int whichDay = 0;
    private final String WHICH_DAY = "WHICH_DAY";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weather_frag, container, false);

        forecastList = getArguments().getParcelableArrayList("forecastList");
        selectCity = (City) getArguments().getSerializable("selectCity");
        tempAndPushDate = getArguments().getString("tempAndPushDate");

        initView();

        if (forecastList != null && forecastList.size() > 0 && !TextUtils.isEmpty(tempAndPushDate)) {
            showWeather();
            showTempAndDate();
        }


        return view;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
    }


    public void showWeather() {
        if (!forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(whichDay);
            String weatherText = forecastToday.getWeatherText();
            temp1.setText(forecastToday.getLow());
            temp2.setText(forecastToday.getHigh());
            weather_text.setText(weatherText);
            WeatherActivity.toolbar.setSubtitle(selectCity.getCity_name());
            cal_date.setText(forecastList.get(whichDay).getDate().substring(0, 6));

            date.setVisibility(View.VISIBLE);
            weather_info.setVisibility(View.VISIBLE);
            weather_more_info.setVisibility(View.VISIBLE);
            public_layout.setVisibility(View.VISIBLE);
            showWeatherPic(weatherText);
        } else {

        }

    }


    private void showWeatherPic(String weatherText) {
        if (weatherText.contains("Thunderstorms")) {
            weatherPic.setImageResource(R.drawable.weather_thunderstorm);

        } else if (weatherText.contains("Cloudy")) {
            weatherPic.setImageResource(R.drawable.weather_cloudy);
        } else if (weatherText.contains("Sunny")) {
            weatherPic.setImageResource(R.drawable.weather_sun_day);

        } else if (weatherText.contains("Showers") || weatherText.contains("Rain")) {
            weatherPic.setImageResource(R.drawable.weather_rain);
        } else if (weatherText.contains("Breezy")) {
            weatherPic.setImageResource(R.drawable.weather_wind);
        }


    }

    public void showTempAndDate() {
        if (!TextUtils.isEmpty(tempAndPushDate)) {

            String[] t = tempAndPushDate.split("\\|");
            temp_now.setText(t[0]);
//            String date = t[1].replace(" CST", "").replaceAll(" \\d{4}", "");
            String d = t[1].substring(17, 25);
            date.setText(d);
        }
    }

    private void initView() {
        date = (TextView) view.findViewById(R.id.public_data);
        temp_now = (TextView) view.findViewById(R.id.temp_now);
        temp1 = (TextView) view.findViewById(R.id.temp1);
        temp2 = (TextView) view.findViewById(R.id.temp2);
        weather_text = (TextView) view.findViewById(R.id.weather_text);
        cal_date = (TextView) view.findViewById(R.id.date_textview);
        weatherPic = (ImageView) view.findViewById(R.id.weather_pic);
        weather_info = (RelativeLayout) view.findViewById(R.id.weather_info);
        public_layout = (LinearLayout) view.findViewById(R.id.public_layout);
        weather_more_info = (RelativeLayout) view.findViewById(R.id.main_info);

        date.setVisibility(View.INVISIBLE);
        weather_info.setVisibility(View.INVISIBLE);
        weather_more_info.setVisibility(View.INVISIBLE);
        public_layout.setVisibility(View.INVISIBLE);
        weather_info.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_info:
                WeatherActivity.queryWeatherFromServer(selectCity);
                WeatherActivity.toolbar.setSubtitle(R.string.loading);
                break;
        }
    }

    public void getHandle(GetHandleCallBack callBack) {
        callBack.onResult(handler);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WeatherActivity.SHOW_WEATHER:
                    forecastList = WeatherDB.loadForecast();
                    showWeather();

                    //WeatherActivity.setupDrawerContent();
                    Snackbar.make(WeatherActivity.fab, mContext.getString(R.string.load_finish), Snackbar.LENGTH_LONG)
                            .show();
                    break;
                case WeatherActivity.SHOW_TEMP_DATE:
                    tempAndPushDate = WeatherDB.loadTempAndDate();
                    showTempAndDate();
                    break;

            }
        }
    };

    public interface GetHandleCallBack {
        void onResult(Handler handler);
    }


}



