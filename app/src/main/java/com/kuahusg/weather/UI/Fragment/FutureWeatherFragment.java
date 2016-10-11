package com.kuahusg.weather.UI.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.kuahusg.weather.model.bean.Forecast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by kuahusg on 16-9-29.
 */

public class FutureWeatherFragment extends BaseFragment implements IFutureWeatherFragView {


    private View view;
    private RecyclerView mRvForecastList;

    private RvAdapter mRvAdapter;

    private IFutureWeatherFragPresenter mPresenter;


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

        if (mRvAdapter != null) {
            mRvAdapter.setData(forecastList);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        initView();

        return view;
    }

    @Override
    public void scrollToTop() {
        if (mRvForecastList != null) {
            mRvForecastList.smoothScrollToPosition(0);

        }
    }

    private void initView() {

        mRvAdapter = new RvAdapter(null);
        mRvForecastList = (RecyclerView) view.findViewById(R.id.rv_forecast_info_list);
        mRvForecastList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvForecastList.setItemAnimator(new DefaultItemAnimator());
        mRvForecastList.setAdapter(mRvAdapter);

    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView info;
        TextView date;
        TextView temp;
        ImageView background_img;

        public ViewHolder(View itemView, ImageView pic, TextView info, TextView date, TextView temp, ImageView background_img) {
            super(itemView);
            this.pic = pic;
            this.info = info;
            this.date = date;
            this.temp = temp;
            this.background_img = background_img;
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

    }

    private class RvAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Forecast> forecastList;

        private ImageView pic;
        private TextView info;
        private TextView temp;
        private TextView date;
        private ImageView background_img;

        public RvAdapter(List<Forecast> forecastList) {
            this.forecastList = forecastList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View cardView = LayoutInflater.from(getContext()).inflate(R.layout.include_weather_card, parent, false);
            pic = (ImageView) cardView.findViewById(R.id.weaher_pic);
            info = (TextView) cardView.findViewById(R.id.weather_info);
            temp = (TextView) cardView.findViewById(R.id.weather_temp);
            date = (TextView) cardView.findViewById(R.id.weather_date);
            background_img = (ImageView) cardView.findViewById(R.id.card_background);
            return new ViewHolder(cardView, pic, info, date, temp, background_img);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Forecast forecast = this.forecastList.get(position);
            Date today = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd", Locale.SIMPLIFIED_CHINESE);
            String date_string = simpleDateFormat.format(new Date(today.getTime()
                    - (position - 1) * 24 * 60 * 60 * 1000));

            pic = holder.getPic();
            info = holder.getInfo();
            temp = holder.getTemp();
            date = holder.getDate();
            background_img = holder.getBackground_img();

            temp.setText(forecast.getLow() + "~" + forecast.getHigh());
            info.setText(forecast.getText());
            date.setText(forecast.getDate().substring(0, 6));
            initImg(pic, info.getText().toString());
            Glide.with(getActivity()).load("http://s.tu.ihuan.me/bgc/" + date_string + ".png")
                    .placeholder(R.drawable.back).into(background_img);
        }

        @Override
        public int getItemCount() {
            return forecastList == null ? 0 : forecastList.size();
        }

        public void setData(List<Forecast> forecastList) {
            this.forecastList = forecastList;
            notifyDataSetChanged();
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
    }
}
