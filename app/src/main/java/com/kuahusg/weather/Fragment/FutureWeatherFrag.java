package com.kuahusg.weather.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.R;
import com.kuahusg.weather.model.Forecast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kuahusg on 16-5-26.
 */

public class FutureWeatherFrag extends Fragment {
    CardView cardView;
    private View view;
    private List<Forecast> forecastList;
    private ImageView pic;
    private TextView info;
    private TextView temp;
    private TextView date;
    private ImageView background_img;
    private int i = 1;
    private Date today = new Date();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");



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
        cardView = (CardView) view.findViewById(R.id.first_card);
        initView(cardView);

        cardView = (CardView) view.findViewById(R.id.second_card);
        initView(cardView);

        cardView = (CardView) view.findViewById(R.id.third_card);
        initView(cardView);

        cardView = (CardView) view.findViewById(R.id.fourth_card);
        initView(cardView);

        cardView = (CardView) view.findViewById(R.id.fifth_card);
        initView(cardView);

    }


    private void initView(CardView cardView) {
        pic = (ImageView) cardView.findViewById(R.id.weaher_pic);
        info = (TextView) cardView.findViewById(R.id.weather_info);
        temp = (TextView) cardView.findViewById(R.id.weather_temp);
        date = (TextView) cardView.findViewById(R.id.weather_date);
        background_img = (ImageView) cardView.findViewById(R.id.card_background);
//        Glide.with(this).load("http://s.tu.ihuan.me/bgc/.png").into(background_img);
        initCard(pic, info, temp, date, background_img);
    }


    private void initCard(ImageView img, TextView info, TextView temp, TextView date, ImageView background) {

        String date_string = simpleDateFormat.format(new Date(today.getTime() - (i - 1) * 24 * 60 * 60 * 1000));

        Forecast forecast;
        if (forecastList.size() > 0) {

            if (i < forecastList.size()) {
                forecast = forecastList.get(i);
                temp.setText(forecast.getLow() + "~" + forecast.getHigh());
                info.setText(forecast.getText());
                date.setText(forecast.getDate().substring(0, 6));
                initImg(img, info.getText().toString());
                Glide.with(getActivity()).load("http://s.tu.ihuan.me/bgc/" + date_string + ".png").placeholder(R.drawable.back).into(background);

                i++;
            }
        }


    }

    private void initImg(ImageView img, String info) {
        if (info.contains("Thunderstorms")) {
//            img.setImageResource(R.drawable.weather_thunderstorm);
            Glide.with(getActivity()).load(R.drawable.weather_thunderstorm).into(img);

        } else if (info.contains("Cloudy")) {
//            img.setImageResource(R.drawable.weather_cloudy);
            Glide.with(getActivity()).load(R.drawable.weather_cloudy).into(img);
        } else if (info.contains("Sunny")) {
//            img.setImageResource(R.drawable.weather_sun_day);
            Glide.with(getActivity()).load(R.drawable.weather_sun_day).into(img);

        } else if (info.contains("Showers") || info.contains("Rain")) {
//            img.setImageResource(R.drawable.weather_rain);
            Glide.with(getActivity()).load(R.drawable.weather_rain).into(img);
        } else if (info.contains("Breezy")) {
//            img.setImageResource(R.drawable.weather_wind);
            Glide.with(getActivity()).load(R.drawable.weather_wind).into(img);
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
