package com.kuahusg.weather.Presenter.interfaceOfPresenter;

import android.app.Activity;

import com.kuahusg.weather.Presenter.base.IBasePresenter;

/**
 * Created by kuahusg on 16-9-27.
 */

public interface IAboutMePresenter extends IBasePresenter {
    @Override
    void init();

    @Override
    void start();

    @Override
    void onDestroy();

    void onClickFab(Activity activity);


}
