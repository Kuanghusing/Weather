package com.kuahusg.weather.Presenter.base;

import com.kuahusg.weather.UI.base.IBaseView;

/**
 * Created by kuahusg on 16-9-27.
 */

public abstract class BasePresenter implements IBasePresenter {

    private IBaseView mView;

    public BasePresenter(IBaseView view) {
        this.mView = view;
    }

    public IBaseView getView() {
        return mView;
    }

    public boolean hasView() {
        return mView != null;
    }


    @Override
    public void onDestroy() {
        mView = null;
    }


}
