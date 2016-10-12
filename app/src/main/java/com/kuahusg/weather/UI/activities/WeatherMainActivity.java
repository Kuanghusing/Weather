package com.kuahusg.weather.UI.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kuahusg.weather.Presenter.WeatherViewPresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IWeatherViewPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.Fragment.FutureWeatherFragment;
import com.kuahusg.weather.UI.Fragment.WeatherFragment;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.interfaceOfView.IWeatherMainView;
import com.kuahusg.weather.model.bean.City;
import com.kuahusg.weather.model.bean.Forecast;
import com.kuahusg.weather.model.bean.ForecastInfo;

import java.util.ArrayList;
import java.util.List;

import static com.kuahusg.weather.util.Constant.BUNDLE_KEY_CITY_NAME;
import static com.kuahusg.weather.util.Constant.BUNDLE_NAME_CITY;
import static com.kuahusg.weather.util.Constant.REQUEST_CODE_SELECT_LOCATION;


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
    private FloatingActionButton fab;

    private PagerAdapter mPagerAdapter;

    private IWeatherViewPresenter mPresenter;
    private Listener listener;

    private boolean first = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (hasPresenter()) {
            mPresenter.init();
        }
        initView();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPagerAdapter == null) {
            setupViewPager();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (first) {
            first = false;
            mSwipeRefreshLayout.setRefreshing(true);
            if (hasPresenter())
                mPresenter.start();
        }
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
//        if (!mSwipeRefreshLayout.isRefreshing())
//            mSwipeRefreshLayout.setRefreshing(true);

    }

    @Override
    public void start() {

    }


    @Override
    public void goToSelectLocationActivity() {
        Intent intent = new Intent(this, SelectLocationActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, REQUEST_CODE_SELECT_LOCATION);


    }

    @Override
    public void loadWeatherDataSourceFinish(List<Forecast> forecasts, ForecastInfo info) {
        if (isFragmentAvaliable()) {
            mSwipeRefreshLayout.setRefreshing(false);
            mWeatherFragment.showWeather(forecasts);
            mWeatherFragment.showForecastInfo(info);

            mFutureWeatherFragment.showForecast(forecasts);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_LOCATION && resultCode == RESULT_OK) {
            Bundle bundle = data.getBundleExtra(BUNDLE_NAME_CITY);
            City city = (City) bundle.get(BUNDLE_KEY_CITY_NAME);

            if (hasPresenter()) {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresenter.refreshWeather(city);
                mPresenter.selectCitySuccess(city);
            }

        }
    }

    @Override
    public void loadWeatherError(String message) {
        mSwipeRefreshLayout.setRefreshing(false);
        mFutureWeatherFragment.error(message);
        mWeatherFragment.error(message);

        Snackbar snackbar = Snackbar.make(fab, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPresenter())
                    mPresenter.refreshWeather();
            }
        }).show();
    }

    @Override
    public void setToolbarSubTitle(String title) {
        mToolbar.setSubtitle(title);
    }

    @Override
    public void showAlertDialog(String title, String message, String negativeString, String positiveString, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(positiveString, listener)
                .show();
    }


    @Override
    public void error(String message) {
        Snackbar.make(fab, message, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_setting:
//                Toast.makeText(this, "还没完成...", Toast.LENGTH_LONG).show();
                Intent i = new Intent(WeatherMainActivity.this, SettingActivity.class);
                startActivity(i);
                break;
            case R.id.change_btn:

/*                showAlertDialog(this.getString(R.string.sure), this.getString(R.string.switch_city),
                        this.getString(R.string.no), this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (hasPresenter())
                                    mPresenter.goToSelectLocationActivity();
                            }
                        });*/
                if (hasPresenter())
                    mPresenter.goToSelectLocationActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        listener = new Listener();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(listener);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setOnClickListener(listener);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_container);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(listener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(listener);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_paper);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
    }


    private void setupViewPager() {
        mWeatherFragment = new WeatherFragment();
        mFutureWeatherFragment = new FutureWeatherFragment();
        List<String> list = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();
        list.add(getString(R.string.today));
        list.add(getString(R.string.future));
        fragmentList.add(mWeatherFragment);
        fragmentList.add(mFutureWeatherFragment);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList, list);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(listener);

    }

    private boolean isFragmentAvaliable() {
        return this.mPagerAdapter != null && (mWeatherFragment = (WeatherFragment) mPagerAdapter.getItem(0)) != null && (mFutureWeatherFragment = (FutureWeatherFragment) mPagerAdapter.getItem(1)) != null;
    }


    private class Listener implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {
        @Override
        public void onRefresh() {
            mPresenter.refreshWeather();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab:
                    mSwipeRefreshLayout.setRefreshing(true);
                    mPresenter.onClickFab();
                    break;
                case R.id.toolbar:
                    if (mViewPager.getCurrentItem() == 0)
                        mWeatherFragment.scrollToTop();
                    else
                        mFutureWeatherFragment.scrollToTop();
                    break;
            }
        }


        /*
            ViewPager Listener
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                mSwipeRefreshLayout.setEnabled(true);
                if (mViewPager.getCurrentItem() == 0) {
//                    mWeatherFragment.scrollToTop();

                } else if (mViewPager.getCurrentItem() == 1) {
//                    mFutureWeatherFragment.scrollToTop();
                }

            } else {
                if (!mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setEnabled(false);
            }
        }

        /*
            navigationView
         */

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    item.setChecked(true);
                    switch (item.getItemId()) {


                        case R.id.today:
                            mViewPager.setCurrentItem(0);
                            break;
                        case R.id.future:
                            mViewPager.setCurrentItem(1);
                            break;
                        case R.id.about:
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i = new Intent(WeatherMainActivity.this, AboutMeActivity.class);
                                    startActivity(i);
                                }
                            }, 250);
                            break;
                    }

                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawers();
                    }

                    return false;
                }
            });
            return false;
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {
        List<Fragment> list;
        List<String> titleList;

        PagerAdapter(FragmentManager fm, List<Fragment> list, List<String> titleList) {
            super(fm);
            this.list = list;
            this.titleList = titleList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return this.list.get(position);
        }


        @Override
        public int getCount() {
            return list.size();
        }
    }
}


