package com.kuahusg.weather.Presenter;

import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IFutureWeatherFragPresenter;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.IFutureWeatherFragView;
import com.kuahusg.weather.data.IDataSource;

/**
 * Created by kuahusg on 16-9-29.
 */

public class FutureWeatherFragPresenterImpl extends BasePresenter implements IFutureWeatherFragPresenter {
    private IFutureWeatherFragView mView;

    public FutureWeatherFragPresenterImpl(IBaseView view) {
        super(view);
        mView = (IFutureWeatherFragView) view;
    }

    @Override
    public void init() {
        super.init();
        if (hasView())
            mView.init();

    }

    @Override
    protected IDataSource setDataSource() {
        return null;
    }

    @Override
    public void start() {

    }
}
