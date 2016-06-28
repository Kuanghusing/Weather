package com.kuahusg.weather.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.R;
import com.kuahusg.weather.activities.WeatherActivity;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-5-25.
 */

public class WeatherFragment extends Fragment {
    //    private final String WHICH_DAY = "WHICH_DAY";
    private View view;
    private List<Forecast> forecastList;
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
    private LinearLayout date_layout;
    private RelativeLayout weather_more_info;
    private ContentLoadingProgressBar progressBar;
    private int whichDay = 0;
    private ForecastInfo info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weather_frag, container, false);
        progressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progressbar);
        mContext = getActivity();

        forecastList = getArguments().getParcelableArrayList("forecastList");
//        selectCity = (City) getArguments().getSerializable("selectCity");
        info = (ForecastInfo) getArguments().getSerializable("ForecastInfo");
        tempAndPushDate = getArguments().getString("tempAndPushDate");

        initView();

        if (forecastList != null && forecastList.size() > 0 && !TextUtils.isEmpty(tempAndPushDate)) {
            showWeather(forecastList);
            showForecastInfo(tempAndPushDate);
        }

//        getFragmentManager().beginTransaction().replace()

        return view;


    }


    public void showWeather(List<Forecast> list) {
        if (list != null) {
            forecastList = list;
        }
        if (!forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(whichDay);
            String weatherText = forecastToday.getText();
            temp1.setText(forecastToday.getLow());
            temp2.setText(forecastToday.getHigh());
            weather_text.setText(weatherText);
            WeatherActivity.toolbar.setSubtitle(selectCity.getCity_name());
            cal_date.setText(forecastList.get(whichDay).getDate().substring(0, 6));

            date.setVisibility(View.VISIBLE);
            weather_info.setVisibility(View.VISIBLE);
            weather_more_info.setVisibility(View.VISIBLE);
            public_layout.setVisibility(View.VISIBLE);
            date_layout.setVisibility(View.VISIBLE);
            showWeatherPic(weatherText);
        } else {
            Snackbar.make(WeatherActivity.fab, mContext.getString(R.string.error_network), Snackbar.LENGTH_LONG).show();


        }

    }


    private void showWeatherPic(String weatherText) {
        if (weatherText.contains("Thunderstorms")) {
//            weatherPic.setImageResource(R.drawable.weather_thunderstorm);
            Glide.with(this).load(R.drawable.weather_thunderstorm).into(weatherPic);

        } else if (weatherText.contains("Cloudy")) {
//            weatherPic.setImageResource(R.drawable.weather_cloudy);
            Glide.with(this).load(R.drawable.weather_cloudy).into(weatherPic);
        } else if (weatherText.contains("Sunny")) {
//            weatherPic.setImageResource(R.drawable.weather_sun_day);
            Glide.with(this).load(R.drawable.weather_sun_day);

        } else if (weatherText.contains("Showers") || weatherText.contains("Rain")) {
//            weatherPic.setImageResource(R.drawable.weather_rain);
            Glide.with(this).load(R.drawable.weather_rain).into(weatherPic);
        } else if (weatherText.contains("Breezy")) {
//            weatherPic.setImageResource(R.drawable.weather_wind);
            Glide.with(this).load(R.drawable.weather_wind).into(weatherPic);
        }


    }

    public void showForecastInfo(ForecastInfo info) {

        /*if (tempStr != null) {
            tempAndPushDate = tempStr;
        }
        if (!TextUtils.isEmpty(tempAndPushDate)) {

            String[] t = tempAndPushDate.split("\\|");
            temp_now.setText(t[0]);
            String d = t[1].substring(17, 25);
            date.setText(d);
        }*/
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
        date_layout = (LinearLayout) view.findViewById(R.id.date_layout);
        weather_more_info = (RelativeLayout) view.findViewById(R.id.main_info);

        date.setVisibility(View.INVISIBLE);
        weather_info.setVisibility(View.INVISIBLE);
        weather_more_info.setVisibility(View.INVISIBLE);
        public_layout.setVisibility(View.INVISIBLE);
        date_layout.setVisibility(View.INVISIBLE);


    }

    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.show();
        }
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.hide();
        }
    }


/*    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_info:
                WeatherActivity.queryWeatherFromServer(selectCity);
                WeatherActivity.toolbar.setSubtitle(R.string.loading);
                break;
        }
    }*/

    /*public void getHandle(GetHandleCallBack callBack) {
        callBack.onResult(handler);
    }*/

/*    public Handler handler = new Handler() {
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
                    tempAndPushDate = WeatherDB.loadForecastInfo();
                    showForecastInfo();
                    break;

            }
        }
    }*/

/*    public interface GetHandleCallBack {
        void onResult(Handler handler);
    }*/


}



