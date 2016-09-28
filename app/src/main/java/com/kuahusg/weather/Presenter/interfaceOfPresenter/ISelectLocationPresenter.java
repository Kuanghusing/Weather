package com.kuahusg.weather.Presenter.interfaceOfPresenter;

import android.content.Context;

import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.util.CityUtil;

/**
 * Created by kuahusg on 16-9-27.
 */

public interface ISelectLocationPresenter extends IBasePresenter {
    @Override
    void init();

    @Override
    void start();

    @Override
    void onDestroy();

    void loadAllCitiesFromRemoteServer(CityUtil.SolveCityCallback callback, Context context);

    void onClickQueryButton(String cityNameToSearch);

    void onClickResultCityItem(City selectedCity);


}
