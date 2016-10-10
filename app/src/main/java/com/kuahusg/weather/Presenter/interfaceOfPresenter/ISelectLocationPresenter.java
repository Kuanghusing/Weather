package com.kuahusg.weather.Presenter.interfaceOfPresenter;

import android.app.Activity;

import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.model.City;

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

    void onClickQueryButton(String cityNameToSearch);

    void onClickResultCityItem(City selectedCity, Activity activity);


}
