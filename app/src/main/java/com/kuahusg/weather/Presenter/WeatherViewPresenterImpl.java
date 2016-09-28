package com.kuahusg.weather.Presenter;

import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IWeatherViewPresenter;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.IWeatherMainView;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;

import java.util.List;

/**
 * Created by kuahusg on 16-9-27.
 */

public class WeatherViewPresenterImpl extends BasePresenter implements IWeatherViewPresenter {

    private IWeatherMainView mPresenter;

    public WeatherViewPresenterImpl(IBaseView view) {
        super(view);
        mPresenter = (IWeatherMainView) view;
    }

    @Override
    public void init() {

        shouldGotoSelectLocationActivity();

    }

    @Override
    public void start() {

    }

    @Override
    public void startAutoUpdateService() {

    }

    @Override
    public void onClickFab() {

    }

    @Override
    public void refreshWeather() {

    }

    @Override
    public void requireAnotherCity() {

    }

    @Override
    public ForecastInfo requireInfo(String woeid) {
        return null;
    }

    @Override
    public List<Forecast> requireForecase(String woeid) {
        return null;
    }

    public void shouldGotoSelectLocationActivity() {

    }


}
