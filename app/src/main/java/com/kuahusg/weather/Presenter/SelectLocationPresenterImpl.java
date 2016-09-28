package com.kuahusg.weather.Presenter;

import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.ISelecLocationView;

/**
 * Created by kuahusg on 16-9-27.
 */

public class SelectLocationPresenterImpl extends BasePresenter implements IBasePresenter {
    private ISelecLocationView mView;

    public SelectLocationPresenterImpl(IBaseView view) {
        super(view);
        mView = (ISelecLocationView) view;

    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }
}
