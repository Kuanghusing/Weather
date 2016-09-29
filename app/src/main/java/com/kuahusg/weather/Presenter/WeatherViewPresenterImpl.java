package com.kuahusg.weather.Presenter;

import android.content.Intent;
import android.content.SharedPreferences;

import com.kuahusg.weather.App;
import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IWeatherViewPresenter;
import com.kuahusg.weather.UI.Fragment.SettingFragment;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.IWeatherMainView;
import com.kuahusg.weather.data.WeatherDataSource;
import com.kuahusg.weather.data.callback.RequestWeatherCallback;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.model.ForecastInfo;
import com.kuahusg.weather.service.AutoUpdateService;
import com.kuahusg.weather.util.PreferenceUtil;

import java.util.List;

import static com.kuahusg.weather.util.PreferenceUtil.PREF_HAS_LOAD_ALL_CITY;

/**
 * Created by kuahusg on 16-9-27.
 */

public class WeatherViewPresenterImpl extends BasePresenter implements IWeatherViewPresenter {
    private WeatherDataSource dataSource;
    private LoadWeatherCallback callback = new LoadWeatherCallback();


    private IWeatherMainView mView;

    public WeatherViewPresenterImpl(IBaseView view) {
        super(view);
        mView = (IWeatherMainView) view;

        dataSource = new WeatherDataSource();
    }

    @Override
    public void init() {

        shouldGotoSelectLocationActivity();

    }

    @Override
    public void start() {
        //get data

    }

    @Override
    public void startAutoUpdateService() {


        Intent intent = new Intent(App.getContext(), AutoUpdateService.class);
        if (PreferenceUtil.getInstance().getSharedPreferences().getBoolean(SettingFragment.AUTO_UPDATE, false)) {

            App.getContext().startService(intent);
        } else {
            App.getContext().stopService(intent);
        }
    }

    @Override
    public void onClickFab() {
        refreshWeather();
    }

    //日常甩锅
    @Override
    public void refreshWeather() {
        dataSource.queryWeather(callback);
    }

    @Override
    public void refreshWeather(City city) {
        dataSource.queryWeather(city.getWoeid(), callback);
    }

    @Override
    public void selectCitySuccess(City selectedCity) {
        SharedPreferences.Editor editor = PreferenceUtil.getInstance().getSharedPreferencesEditor();
        editor.putString(PreferenceUtil.PREF_SELECTED_CITY, selectedCity.getFullNmae())
                .apply();

    }

    private void shouldGotoSelectLocationActivity() {
        if (isFirstToOpen()) {
            if (hasView())
                mView.goToSelectLocationActivity();

        }


    }

    private boolean isFirstToOpen() {
        SharedPreferences sharedPreferences = PreferenceUtil.getInstance().getSharedPreferences();
        boolean hasLoadAllCity = sharedPreferences.getBoolean(PREF_HAS_LOAD_ALL_CITY, false);

        return !hasLoadAllCity;
    }

    private class LoadWeatherCallback implements RequestWeatherCallback {


        @Override
        public void error(String message) {
            if (hasView())
                mView.loadWeatherError(message);

        }

        @Override
        public void success(List<Forecast> forecasts, ForecastInfo forecastInfo) {
            if (hasView())
                mView.loadWeatherDataSourceFinish(forecasts, forecastInfo);
        }
    }


}
