package com.kuahusg.weather.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuahusg.weather.R;
import com.kuahusg.weather.model.Forecast;

import java.util.List;

/**
 * Created by kuahusg on 16-5-26.
 */

public class FutureWeatherFrag extends Fragment {
    private View view;
    private List<Forecast> forecastList;
    private ImageView img;
    private TextView info;
    private TextView temp;
    private TextView date;

    private int i = 1;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.future_frag, container, false);
        Bundle date = getArguments();
        forecastList = date.getParcelableArrayList("forecastList");
        initId();

        return view;
    }

    private void initId() {
        i = 1;
        initView(R.id.first_pic, R.id.first_info, R.id.first_temp, R.id.first_date);
        initView(R.id.second_pic, R.id.second_info, R.id.second_temp, R.id.second_date);
        initView(R.id.third_pic, R.id.third_info, R.id.third_temp, R.id.third_date);
        initView(R.id.fourth_pic, R.id.fourth_info, R.id.fourth_temp, R.id.fourth_date);
        initView(R.id.fifth_pic, R.id.fifth_info, R.id.fifth_temp, R.id.fifth_date);
    }


    private void initView(int imgId, int infoId, int tempId, int dateId) {

        img = (ImageView) view.findViewById(imgId);
        info = (TextView) view.findViewById(infoId);
        temp = (TextView) view.findViewById(tempId);
        date = (TextView) view.findViewById(dateId);


        initCard(img, info, temp, date);
    }


    private void initCard(ImageView img, TextView info, TextView temp, TextView date) {

        Forecast forecast;
        if (forecastList.size() > 0) {

            if (i < forecastList.size()) {
                forecast = forecastList.get(i);
                temp.setText(forecast.getLow() + "~" + forecast.getHigh());
                info.setText(forecast.getWeatherText());
                date.setText(forecast.getDate().substring(0, 6));
                initImg(img, info.getText().toString());
                i++;

            }
        }


    }

    private void initImg(ImageView img, String info) {
        if (info.contains("Thunderstorms")) {
            img.setImageResource(R.drawable.weather_thunderstorm);

        } else if (info.contains("Cloudy")) {
            img.setImageResource(R.drawable.weather_cloudy);
        } else if (info.contains("Sunny")) {
            img.setImageResource(R.drawable.weather_sun_day);

        } else if (info.contains("Showers") || info.contains("Rain")) {
            img.setImageResource(R.drawable.weather_rain);
        } else if (info.contains("Breezy")) {
            img.setImageResource(R.drawable.weather_wind);
        }

    }

    public synchronized void refreshWeather(List<Forecast> list) {
        if (list != null) {
            forecastList = list;
        }
        initId();
        i = 1;

    }
}
