package com.kuahusg.weather.UI.Fragment.rebuild;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.Presenter.FutureWeatherFragPresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IFutureWeatherFragPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.base.BaseFragment;
import com.kuahusg.weather.UI.interfaceOfView.IFutureWeatherFragView;
import com.kuahusg.weather.model.Forecast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kuahusg on 16-9-29.
 */

public class FutureWeatherFragment extends BaseFragment implements IFutureWeatherFragView {

    private CardView cardView;
    private CardView cardView2;
    private CardView cardView3;
    private CardView cardView4;
    private CardView cardView5;

    private View view;
    private List<Forecast> forecastList;
    private ImageView pic;
    private TextView info;
    private TextView temp;
    private TextView date;
    private ImageView background_img;
    private ImageView imageView;
    private int index = 1;
    private NestedScrollView nestedScrollView;

    private IFutureWeatherFragPresenter mPresenter;

    private static final int count = 5;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_future;
    }

    @Override
    protected IBasePresenter setPresenter() {
        mPresenter = new FutureWeatherFragPresenterImpl(this);
        return mPresenter;

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
    public void showForecast(List<Forecast> forecastList) {

        initCard(cardView, forecastList.get(0));
        initCard(cardView2, forecastList.get(1));
        initCard(cardView3, forecastList.get(2));
        initCard(cardView4, forecastList.get(3));
        initCard(cardView5, forecastList.get(4));
        // TODO: 16-9-29 ...

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        initView();

        return view;
    }

    private void initView() {

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);

        cardView = (CardView) view.findViewById(R.id.first_card);
        initView(cardView);

        cardView2 = (CardView) view.findViewById(R.id.second_card);
        initView(cardView2);

        cardView3 = (CardView) view.findViewById(R.id.third_card);
        initView(cardView3);

        cardView4 = (CardView) view.findViewById(R.id.fourth_card);
        initView(cardView4);

        cardView5 = (CardView) view.findViewById(R.id.fifth_card);
        initView(cardView5);
    }

    private void initView(CardView cardView) {
        pic = (ImageView) cardView.findViewById(R.id.weaher_pic);
        info = (TextView) cardView.findViewById(R.id.weather_info);
        temp = (TextView) cardView.findViewById(R.id.weather_temp);
        date = (TextView) cardView.findViewById(R.id.weather_date);
        background_img = (ImageView) cardView.findViewById(R.id.card_background);
        cardView.setTag(new ViewHolder(pic, info, date, temp, background_img, index++));

    }

    private void initCard(CardView cardView, Forecast forecast) {
        ViewHolder v = (ViewHolder) cardView.getTag();
        ImageView img = v.getPic();

        TextView info = v.getInfo();
        TextView temp = v.getTemp();
        TextView date = v.getDate();
        ImageView background = v.getBackground_img();
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
        String date_string = simpleDateFormat.format(new Date(today.getTime() - (index - 1) * 24 * 60 * 60 * 1000));

        temp.setText(forecast.getLow() + "~" + forecast.getHigh());
        info.setText(forecast.getText());
        date.setText(forecast.getDate().substring(0, 6));
        initImg(img, info.getText().toString());
        Glide.with(getActivity()).load("http://s.tu.ihuan.me/bgc/" + date_string + ".png").placeholder(R.drawable.back).into(background);
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

    private class ViewHolder {
        ImageView pic;
        TextView info;
        TextView date;
        TextView temp;
        ImageView background_img;
        int index;

        public ViewHolder(ImageView pic, TextView info, TextView date, TextView temp, ImageView background_img, int index) {
            this.pic = pic;
            this.info = info;
            this.date = date;
            this.temp = temp;
            this.background_img = background_img;
            this.index = index;
        }

        public TextView getTemp() {
            return temp;
        }

        public ImageView getPic() {
            return pic;
        }

        public TextView getInfo() {
            return info;
        }

        public TextView getDate() {
            return date;
        }

        public ImageView getBackground_img() {
            return background_img;
        }

        public int getIndex() {
            return index;
        }
    }
}
