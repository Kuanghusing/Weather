package com.kuahusg.weather.Presenter;

import android.content.Context;

import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.ISelectLocationPresenter;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.ISelectLocationView;
import com.kuahusg.weather.data.WeatherDataSource;
import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.util.CityUtil;
import com.kuahusg.weather.util.PreferenceUtil;

import java.util.List;

/**
 * Created by kuahusg on 16-9-27.
 */

public class SelectLocationPresenterImpl extends BasePresenter implements ISelectLocationPresenter {
    private ISelectLocationView mView;
    private WeatherDataSource dataSource;

    public SelectLocationPresenterImpl(IBaseView view) {
        super(view);

        mView = (ISelectLocationView) view;
        dataSource = new WeatherDataSource();

    }

    @Override
    public void init() {
        if (shouldGetAllCity()) {
            dataSource.loadAllCity(new RequestCityCallback() {
                @Override
                public void success(List<String> cityList) {
                    if (hasView())
                        mView.loadAllCityFinish(cityList);

                }

                @Override
                public void error() {

                }
            });
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void loadAllCitiesFromRemoteServer(CityUtil.SolveCityCallback callback, Context context) {

    }

    @Override
    public void onClickQueryButton(String cityNameToSearch) {

    }

    @Override
    public void onClickResultCityItem(City selectedCity, Context context) {

    }

    private boolean shouldGetAllCity() {
        return !PreferenceUtil.getInstance().getSharedPreferences().getBoolean(PreferenceUtil.PREF_HAS_LOAD_ALL_CITY, false);
    }
}
