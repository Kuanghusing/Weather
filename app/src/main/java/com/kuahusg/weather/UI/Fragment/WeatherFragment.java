package com.kuahusg.weather.UI.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.Widget.BackgroundPicture;
import com.kuahusg.weather.UI.activities.WeatherActivity;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.util.DateUtil;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.WeatherUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by kuahusg on 16-5-25.
 */

public class WeatherFragment extends Fragment implements View.OnClickListener, WeatherUtil.GetWeatherCallback {
    private View view;
    private List<Forecast> forecastList;
    private Context mContext;
    private TextView date;
    private TextView temp_now;
    private TextView temp1;
    private TextView temp2;
    private TextView weather_text;
    private TextView cal_date;
    private ImageView weatherPic;
    private TextView wind_speed;
    private TextView wind_direction;
    private TextView sunrise;
    private TextView sunset;
    private Button check;
    private String link;
    private TextView refresh_time;
    private BackgroundPicture backgroundPicture;


    private RelativeLayout forecast_now_container;
    private LinearLayout forecast_info_container;
    private int whichDay = 0;
    private ForecastInfo info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weather_frag, container, false);
        mContext = getActivity();
        initView();

        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*forecastList = getArguments().getParcelableArrayList("forecastList");
        info = (ForecastInfo) getArguments().getSerializable("ForecastInfo");*/
        if (info != null) {
            link = info.getLink();
        }


        ((WeatherActivity) mContext).getWeatherFromActivity(this);

        /*forecastList = WeatherUtil.loadForecastFromDatabase(selectCity.getWoeid());
        info = WeatherUtil.loadForecastInfoFromDatabase(selectCity.getWoeid());*/

        /*if (forecastList != null && forecastList.size() > 0 && info != null) {

            showWeather(forecastList);
            showForecastInfo(info);*/


    }


    @Override
    public void getWeather(List<Forecast> forecastList) {
        if (forecastList == null) {
            ((WeatherActivity) getActivity()).queryWeatherFromServer(null);
            return;
        }
        showWeather(forecastList);
    }

    @Override
    public void getWeatherInfo(ForecastInfo info) {
        showForecastInfo(info);

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
            cal_date.setText(forecastToday.getDate().substring(0, 6));

//            WeatherActivity.toolbar.setSubtitle(selectCity.getCity_name());


            forecast_info_container.setVisibility(View.VISIBLE);
            forecast_now_container.setVisibility(View.VISIBLE);


            showWeatherPic(weatherText);
        } else {
            Snackbar.make(WeatherActivity.fab, mContext.getString(R.string.error_network), Snackbar.LENGTH_LONG).show();


        }

    }


    private void showWeatherPic(String info) {
        /*Glide.with(mContext).load("http://s.tu.ihuan.me/bgc/" + DateUtil.getDate("yy-MM-dd") + ".png")
                .placeholder(R.drawable.back).into(today_background);*/

        if (info.contains("Thunderstorms")) {
            weatherPic.setImageResource(R.drawable.thunderstorm);
        } else if (info.contains("Cloudy")) {
            weatherPic.setImageResource(R.drawable.cloud_sun);
        } else if (info.contains("Sunny")) {
            weatherPic.setImageResource(R.drawable.sunny);
        } else if (info.contains("Showers") || info.contains("Rain")) {
            weatherPic.setImageResource(R.drawable.rain3);
        } else if (info.contains("Breezy")) {
            weatherPic.setImageResource(R.drawable.wind);
        } else if (info.contains("snow")) {
            weatherPic.setImageResource(R.drawable.snow2);
        } else {
            weatherPic.setImageResource(R.drawable.sun);
        }


    }

    public void showForecastInfo(ForecastInfo info) {


        if (info != null) {
            sunrise.setText(info.getSunrise());
            sunset.setText(info.getSunset());
            wind_direction.setText(getWindDirection(Integer.valueOf(info.getWindDirection())));
            wind_speed.setText(info.getWindSpeed() + " km/h");
            temp_now.setText(info.getTemp());
            date.setText(info.getDate().substring(16, 22));
            refresh_time.setText(getString(R.string.refersh_time) + "\n" + info.getLastBuildDate().substring(17, 22));
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
        wind_speed = (TextView) view.findViewById(R.id.wind_speed);
        wind_direction = (TextView) view.findViewById(R.id.wind_direction);
        sunrise = (TextView) view.findViewById(R.id.sunrise);
        sunset = (TextView) view.findViewById(R.id.sunset);
//        today_background = (ImageView) view.findViewById(R.id.today_background);
        refresh_time = (TextView) view.findViewById(R.id.refresh_time);
        check = (Button) view.findViewById(R.id.check_source);
        check.setOnClickListener(this);
        forecast_now_container = (RelativeLayout) view.findViewById(R.id.weather_now_container);
        forecast_info_container = (LinearLayout) view.findViewById(R.id.weather_info_container);
        backgroundPicture = (BackgroundPicture) view.findViewById(R.id.today_background);


        backgroundPicture.addOnBackgroundPicClickListener(new BackgroundPicture.OnBackgroundPicClickListener() {
            @Override
            public void OnClickClickPic(ImageView pic) {
//                int from = DateUtil.getDatePart(Calendar.DAY_OF_MONTH, 0);
                int i = getRandomNum(7, 17);
                loadPicture(DateUtil.getDate("yy-MM-dd", i), pic);
            }

            @Override
            public void initBackgroundPic(ImageView pic) {
                LogUtil.v(this.toString(), "initBackgroundPic()");
                loadPicture(DateUtil.getDate("yy-MM-dd"), pic);
            }
        });

        forecast_now_container.setVisibility(View.INVISIBLE);
        forecast_info_container.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_source:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(info.getLink()));
                startActivity(intent);
        }
    }


    private String getWindDirection(int num) {
        if (num > 0 && num < 90) {
            return "东北 " + num;

        } else if (num > 90 && num < 180) {
            return "西北 " + (180 - num);
        } else if (num > 180 && num < 270) {
            return "西南 " + (num - 180);
        } else {
            return "东南 " + (360 - num);
        }
    }


    private int getRandomNum(int from, int to) {
        Random random = new Random();
        return (random.nextInt(to - from) + from);
    }

    private void loadPicture(String s, ImageView imageView) {
        Glide.with(mContext).load("http://s.tu.ihuan.me/bgc/" + s + ".png")
                .placeholder(R.drawable.back)
                .into(imageView);
    }

}



