package com.kuahusg.weather.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.activities.WeatherActivity;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.util.WeatherUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by kuahusg on 16-5-26.
 */

public class FutureWeatherFragment extends Fragment implements WeatherUtil.GetWeatherCallback, NestedScrollView.OnScrollChangeListener {
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
    private Context mContext;
    private NestedScrollView nestedScrollView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_future, container, false);


        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(this);
        ((WeatherActivity) getActivity()).getWeatherFromActivity(this);

        return view;
    }

    public void initView(List<Forecast> forecastList) {
        if (forecastList != null) {
            this.forecastList = forecastList;
        }
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
//                        LogUtil.v("FutureWeatherFragment", "i:" + i);
                    }
                });*/

                i++;
            }
        }


    }

    private void initImg(ImageView img, String info) {
        if (info.contains("Thunderstorms")) {
            img.setImageResource(R.drawable.thunderstorm);


        } else if (info.contains("Cloudy")) {
            img.setImageResource(R.drawable.cloud_sun);
        } else if (info.contains("Sunny")) {
            img.setImageResource(R.drawable.sunny);

        } else if (info.contains("Showers") || info.contains("Rain")) {
            img.setImageResource(R.drawable.rain3);
        } else if (info.contains("Breezy")) {
            img.setImageResource(R.drawable.wind);
        } else if (info.contains("snow")) {
            img.setImageResource(R.drawable.snow2);
        } else {
            img.setImageResource(R.drawable.sun);
        }

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

    @Override
    public void getWeather(List<Forecast> forecastList) {
        initView(forecastList);
    }

    @Override
    public void getWeatherInfo(ForecastInfo info) {

    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (oldScrollY < scrollY) {
//            WeatherActivity.fab.hide();

        } else {
//            WeatherActivity.fab.show();
        }
    }
}
