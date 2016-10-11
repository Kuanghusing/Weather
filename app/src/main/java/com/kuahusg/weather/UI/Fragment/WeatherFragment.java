package com.kuahusg.weather.UI.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuahusg.weather.Presenter.WeatherFragPresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IWeatherMainFragPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.Widget.BackgroundPictureView;
import com.kuahusg.weather.UI.base.BaseFragment;
import com.kuahusg.weather.UI.interfaceOfView.IWeatherFragView;
import com.kuahusg.weather.model.bean.Forecast;
import com.kuahusg.weather.model.bean.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-9-29.
 */

public class WeatherFragment extends BaseFragment implements IWeatherFragView, View.OnClickListener {
    private IWeatherMainFragPresenter mPresenter;

    private NestedScrollView nestedScrollView;
    private View view;
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
    private BackgroundPictureView backgroundPictureView;

    private RelativeLayout forecast_now_container;
    private LinearLayout forecast_info_container;


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_weather_main;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void finish() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        this.mPresenter = new WeatherFragPresenterImpl(this);
        return mPresenter;
    }

    @Override
    public void showWeather(List<Forecast> forecastList) {
        if (forecastList != null && !forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(0);
            String weatherText = forecastToday.getText();
            temp1.setText(forecastToday.getLow());
            temp2.setText(forecastToday.getHigh());
            weather_text.setText(weatherText);
            cal_date.setText(forecastToday.getDate().substring(0, 6));

            forecast_info_container.setVisibility(View.VISIBLE);
            forecast_now_container.setVisibility(View.VISIBLE);

            showWeatherPic(weatherText);
        }
    }

    @Override
    public void showForecastInfo(ForecastInfo info) {
        if (info != null) {
            sunrise.setText(info.getSunrise());
            sunset.setText(info.getSunset());
            wind_direction.setText(getWindDirection(Integer.valueOf(info.getWindDirection())));
            wind_speed.setText(info.getWindSpeed() + " km/h");
            temp_now.setText(info.getTemp());
            date.setText(info.getDate().substring(16, 22));
            refresh_time.setText(getString(R.string.refersh_time) + "\n" + info.getLastBuildDate().substring(17, 22));
            link = info.getLink();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = super.onCreateView(inflater, container, savedInstanceState);


        initView();
        return this.view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_source:
                if (hasPresenter())
                    mPresenter.checkForecastSource(this.link, getContext());
                break;
        }
    }

    private void initView() {

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nscrollView);
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
        refresh_time = (TextView) view.findViewById(R.id.refresh_time);
        check = (Button) view.findViewById(R.id.check_source);
        check.setOnClickListener(this);
        forecast_now_container = (RelativeLayout) view.findViewById(R.id.weather_now_container);
        forecast_info_container = (LinearLayout) view.findViewById(R.id.weather_info_container);
        backgroundPictureView = (BackgroundPictureView) view.findViewById(R.id.today_background);


        backgroundPictureView.addOnBackgroundPicClickListener(new BackgroundPictureView.OnBackgroundPicClickListener() {
            @Override
            public void OnClickClickPic(ImageView pic) {
                if (hasPresenter())
                    mPresenter.onClickBackgroundPic(pic);
            }

            @Override
            public void initBackgroundPic(ImageView pic) {
                if (hasPresenter())
                    mPresenter.initBackgroundPic(pic);

            }
        });

        forecast_now_container.setVisibility(View.INVISIBLE);
        forecast_info_container.setVisibility(View.INVISIBLE);

    }


    private void showWeatherPic(String info) {
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

    @Override
    public void scrollToTop() {
        if (nestedScrollView != null) {
//            nestedScrollView.scrollTo(0, 0);
            nestedScrollView.smoothScrollTo(0, 0);
        }
    }


}
