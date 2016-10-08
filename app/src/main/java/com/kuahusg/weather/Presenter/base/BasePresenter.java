package com.kuahusg.weather.Presenter.base;

import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.data.IDataSource;

import java.lang.ref.WeakReference;

/**
 * Created by kuahusg on 16-9-27.
 */

public abstract class BasePresenter implements IBasePresenter {

    private IBaseView mView;
    //    private IDataSource dataSource;
    private WeakReference<IDataSource> dataSourceWeakReference;

    public BasePresenter(IBaseView view) {
        this.mView = view;
    }

    public IBaseView getView() {
        return mView;
    }

    public boolean hasView() {
        return mView != null;
    }


    protected abstract IDataSource setDataSource();

    public IDataSource getDataSource() {
        if (dataSourceWeakReference.get() != null) {
            return dataSourceWeakReference.get();
        } else
            throw new NullPointerException("datasource not set!");
    }

    @Override
    public void init() {
        dataSourceWeakReference = new WeakReference<>(setDataSource());
    }

    @Override
    public void onDestroy() {
        mView = null;
    }


}
