package com.kuahusg.weather.UI.Fragment;

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
import com.kuahusg.weather.UI.Widget.BackgroundPicture;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.util.DateUtil;
import com.kuahusg.weather.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    private ImageView imageView;
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

        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
        final String date_string = simpleDateFormat.format(new Date(today.getTime() - (i - 1) * 24 * 60 * 60 * 1000));
        final int temp_i = i;

//        String date_string = DateUtil.getDate("yy-MM-dd", i);
        Forecast forecast;
        if (forecastList.size() > 0) {

            if (i < forecastList.size()) {
                forecast = forecastList.get(i);
                temp.setText(forecast.getLow() + "~" + forecast.getHigh());
                info.setText(forecast.getText());
                date.setText(forecast.getDate().substring(0, 6));
                initImg(img, info.getText().toString());
                Glide.with(getActivity()).load("http://s.tu.ihuan.me/bgc/" + date_string + ".png").placeholder(R.drawable.back).into(background);

                /*background.addOnBackgroundPicClickListener(new BackgroundPicture.OnBackgroundPicClickListener() {
                    @Override
                    public void OnClickClickPic(ImageView pic) {
//                        loadPicture(DateUtil.getDate("yy-MM-dd", getRandomNum(10 * temp_i, 10 * temp_i + 20)),pic);
//                        LogUtil.v("FutureFrag", "i:" + i + " RandomNum:" + getRandomNum(7 * i, 7 * i + 6));
                    }

                    @Override
                    public void initBackgroundPic(ImageView pic) {
                        loadPicture(DateUtil.getDate("yy-MM-dd",temp_i),pic);
//                        LogUtil.v("FutureWeatherFrag", "i:" + i);
                    }
                });*/

                i++;
            }
        }


    }

    private void initImg(ImageView img, String info) {
        if (info.contains("Thunderstorms")) {
            img.setImageResource(R.drawable.thunderstorm);
//            Glide.with(getActivity()).load(R.drawable.thunderstorm).into(img);


        } else if (info.contains("Cloudy")) {
            img.setImageResource(R.drawable.cloud_sun);
//            Glide.with(getActivity()).load(R.drawable.cloud_sun).into(img);
        } else if (info.contains("Sunny")) {
            img.setImageResource(R.drawable.sunny);
//            Glide.with(getActivity()).load(R.drawable.sunny).into(img);

        } else if (info.contains("Showers") || info.contains("Rain")) {
            img.setImageResource(R.drawable.rain3);
//            Glide.with(getActivity()).load(R.drawable.rain3).into(img);
        } else if (info.contains("Breezy")) {
            img.setImageResource(R.drawable.wind);
//            Glide.with(getActivity()).load(R.drawable.wind).into(img);
        } else if (info.contains("snow")) {
//            Glide.with(getActivity()).load(R.drawable.snow2).into(img);
            img.setImageResource(R.drawable.snow2);
        } else {
//            Glide.with(getActivity()).load(R.drawable.sun).into(img);
            img.setImageResource(R.drawable.sun);
        }

    }

    public synchronized void refreshWeather(List<Forecast> list) {
        if (list != null) {
            forecastList = list;
        }
        initId();
        i = 1;

    }


    private int getRandomNum(int from, int to) {
        Random random = new Random();
        return (random.nextInt(to - from) + from);
    }

    private void loadPicture(String s, ImageView imageView) {
        Glide.with(this).load("http://s.tu.ihuan.me/bgc/" + s + ".png")
                .placeholder(R.drawable.back)
                .into(imageView);
    }
}
