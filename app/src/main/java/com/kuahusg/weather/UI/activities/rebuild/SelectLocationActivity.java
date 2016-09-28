package com.kuahusg.weather.UI.activities.rebuild;

import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.ISelectLocationPresenter;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.interfaceOfView.ISelecLocationView;
import com.kuahusg.weather.model.City;

import java.util.List;

/**
 * Created by kuahusg on 16-9-27.
 */

public class SelectLocationActivity extends BaseActivity implements ISelecLocationView {
    private ISelectLocationPresenter mPresenter;

    private AutoCompleteTextView mAutoCpTv;
    private ArrayAdapter<String> mAutoCpTvAdapter;
    private ListView mLvCitySearchResult;
    private ArrayAdapter<String> mCitySearchResultAdapter;
    private ProgressDialog mPgd;
    private Button mBtnSearch;
    private Toolbar mToolbar;


    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected int setLayoutId() {
        return 0;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void queryCityError(String message) {

    }

    @Override
    public void finishQueryCity(List<String> list) {

    }

    @Override
    public void finishLoadCityListFromDB(List<City> cityList) {

    }

    @Override
    public void error(String message) {

    }


    private void initView() {

    }


    private void showProgress(boolean cancelable) {

    }
}
