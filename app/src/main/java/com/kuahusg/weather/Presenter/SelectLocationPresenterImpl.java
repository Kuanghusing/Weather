package com.kuahusg.weather.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.ISelectLocationPresenter;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.ISelectLocationView;
import com.kuahusg.weather.data.IDataSource;
import com.kuahusg.weather.data.WeatherDataSource;
import com.kuahusg.weather.data.callback.RequestCityCallback;
import com.kuahusg.weather.data.callback.RequestCityResultCallback;
import com.kuahusg.weather.data.local.LocalForecastDataSource;
import com.kuahusg.weather.data.remote.RemoteForecastDataSource;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.util.PreferenceUtil;

import java.util.List;

import static com.kuahusg.weather.util.Constant.BUNDLE_KEY_CITY_NAME;
import static com.kuahusg.weather.util.Constant.BUNDLE_NAME_CITY;

/**
 * Created by kuahusg on 16-9-27.
 */

public class SelectLocationPresenterImpl extends BasePresenter implements ISelectLocationPresenter, RequestCityResultCallback {
    private ISelectLocationView mView;

    public SelectLocationPresenterImpl(IBaseView view) {
        super(view);

        mView = (ISelectLocationView) view;

    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    protected IDataSource setDataSource() {
        return new WeatherDataSource(new RemoteForecastDataSource(), new LocalForecastDataSource());
    }

    @Override
    public void start() {
        if (shouldGetAllCityFromServer()) {
            if (hasView())
                mView.startLoadingData(false);
        }
        getDataSource().loadAllCity(new RequestCityCallback() {
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


    @Override
    public void onClickQueryButton(String cityNameToSearch) {
        if (hasView())
            mView.startLoadingData(true);
        getDataSource().queryCity(this, cityNameToSearch);
    }

    @Override
    public void onClickResultCityItem(City selectedCity, Activity activity) {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_CITY_NAME, selectedCity);
        data.putExtra(BUNDLE_NAME_CITY, bundle);

        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }


    @Override
    public void success(List<City> cityList) {
        if (hasView())
            mView.finishQueryCity(cityList);
    }

    @Override
    public void error(String message) {
        if (hasView())
            mView.queryCityError(message);
    }

    private boolean shouldGetAllCityFromServer() {
        return !PreferenceUtil.getInstance().getSharedPreferences().getBoolean(PreferenceUtil.PREF_HAS_LOAD_ALL_CITY, false);
    }
}
