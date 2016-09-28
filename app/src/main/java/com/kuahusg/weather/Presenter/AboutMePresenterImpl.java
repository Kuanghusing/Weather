package com.kuahusg.weather.Presenter;

import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IAboutMePresenter;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.IAboutMeView;

/**
 * Created by kuahusg on 16-9-27.
 */

public class AboutMePresenterImpl extends BasePresenter implements IAboutMePresenter {
    private IAboutMeView mView;

    public AboutMePresenterImpl(IBaseView view) {
        super(view);
        mView = (IAboutMeView) view;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void onClickFab() {

    }
}
