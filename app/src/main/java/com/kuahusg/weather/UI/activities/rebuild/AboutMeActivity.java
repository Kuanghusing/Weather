package com.kuahusg.weather.UI.activities.rebuild;

import android.support.v7.widget.Toolbar;

import com.kuahusg.weather.Presenter.AboutMePresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IAboutMePresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.interfaceOfView.IAboutMeView;

/**
 * Created by kuahusg on 16-9-27.
 */

public class AboutMeActivity extends BaseActivity implements IAboutMeView {
    private IAboutMePresenter mPresenter;
    private Toolbar toolbar;


    @Override
    protected IBasePresenter setPresenter() {
        mPresenter = new AboutMePresenterImpl(this);
        return mPresenter;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void init() {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void start() {

    }
}
