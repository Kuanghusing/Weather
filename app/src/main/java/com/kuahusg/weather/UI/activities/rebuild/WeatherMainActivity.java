package com.kuahusg.weather.UI.activities.rebuild;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;

import com.kuahusg.weather.Presenter.WeatherViewPresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IWeatherViewPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.Fragment.FutureWeatherFragment;
import com.kuahusg.weather.UI.Fragment.WeatherFragment;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.interfaceOfView.IWeatherMainView;
import com.kuahusg.weather.model.City;

/**
 * Created by kuahusg on 16-9-27.
 */

public class WeatherMainActivity extends BaseActivity implements IWeatherMainView {

    private Toolbar mToolbar;
    private WeatherFragment mWeatherFragment;
    private FutureWeatherFragment mFutureWeatherFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private City mSelectCity;
    private PagerAdapter mPagerAdapter;

    private IWeatherViewPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPresenter())
            getPresenter().init();
    }

    @Override
    protected IBasePresenter setPresenter() {
        mPresenter = new WeatherViewPresenterImpl(this);
        return mPresenter;
    }


    @Override
    protected int setLayoutId() {
        return R.layout.activity_weather_main;
    }

    @Override
    public void init() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);

    }

    @Override
    public void start() {

    }

    @Override
    public void goToSelectLocationActivity() {

    }

    @Override
    public void loadWeatherError(String message) {

    }

    @Override
    public void showAlertDialog(String title, String message, String negativeString, String positiveString, DialogInterface.OnClickListener listener) {

    }

    @Override
    public void error(String message) {

    }

    private void initView() {

    }


    private void setupDrawerContent() {

    }


    private void setupViewPager(final ViewPager viewPager) {

    }
}
