package com.kuahusg.weather.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kuahusg.weather.Fragment.FutureWeatherFrag;
import com.kuahusg.weather.Fragment.WeatherFragment;
import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Forecast;
import com.kuahusg.weather.service.AutoUpdateService;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-28.
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {


    private static City selectCity;
    private static String tempAndPushDate;
    private static List<Forecast> forecastList;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public static FloatingActionButton fab;
    public static Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static Context mcontext;
//    private static int whichDay = 0;

    public static final int SHOW_WEATHER = 2;
    public static final int SHOW_TEMP_DATE = 3;

    private static WeatherFragment todayFrag;
    private static FutureWeatherFrag futureWeatherFrag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        mcontext = getApplicationContext();




        /*
        * fab
         */

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(this);
        }

        /*
        * toolbar
         */

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("loading...");


        /*
        * tabLayout and viewPager
         */


        drawerLayout = (DrawerLayout) findViewById(R.id.main_container);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);


        InitWeather();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_paper);
        setupViewPager(viewPager, new WeatherFragment(), new FutureWeatherFrag());
        tabLayout.setupWithViewPager(viewPager);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }


        PagerAdapter adapter = (PagerAdapter) viewPager.getAdapter();
        todayFrag = (WeatherFragment) adapter.getItem(0);
        futureWeatherFrag = (FutureWeatherFrag) adapter.getItem(1);


        Intent intent = new Intent(mcontext, AutoUpdateService.class);
        mcontext.startService(intent);


    }


    public static void queryWeatherFromServer(final City selectCity) {
        Utility.queryWeather(selectCity.getWoeid(), mcontext, false);

    }

    private void InitWeather() {
        selectCity = (City) getIntent().getSerializableExtra("selectCity");
        boolean anotherCity = getIntent().getBooleanExtra("anotherCity", false);
        if (selectCity != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("selectCity", selectCity.getFullNmae());
            editor.putString("woeid", selectCity.getWoeid());
            editor.putString("city_name", selectCity.getCity_name());
            editor.apply();

        } else {
            LogUtil.v(this.getClass().getName(), "selectCity is null!");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String fullName = preferences.getString("selectCity", "");
            String woeid = preferences.getString("woeid", "");
            String city_name = preferences.getString("city_name", "");
            LogUtil.v(this.getClass().getName(), fullName + "\t" + woeid);
            selectCity = new City(city_name, woeid, fullName);
        }
        if (anotherCity) {
            queryWeatherFromServer(selectCity);
        }
        tempAndPushDate = WeatherDB.loadTempAndDate();
        forecastList = WeatherDB.loadForecast();


        if (forecastList.size() <= 0 || TextUtils.isEmpty(tempAndPushDate)) {
            queryWeatherFromServer(selectCity);
        } else {

            setupDrawerContent();

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                queryWeatherFromServer(selectCity);
                toolbar.setSubtitle(R.string.loading);
                break;
        }
    }


    private void alertDialog(String title, String message, String negativeString, String positiveString,
                             DialogInterface.OnClickListener listener) {
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


    public void setupDrawerContent() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.today:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.future:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.about:
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent i = new Intent(mcontext, About.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mcontext.startActivity(i);
                            }
                        }, 250);
                        break;

                }


                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                    drawerLayout.closeDrawers();
                }

                return false;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager, WeatherFragment todayFrag, FutureWeatherFrag futureWeatherFrag) {


        Bundle data = new Bundle();
        data.putParcelableArrayList("forecastList", (ArrayList<Forecast>) forecastList);
        data.putString("tempAndPushDate", tempAndPushDate);
        data.putSerializable("selectCity", selectCity);
        todayFrag.setArguments(data);
        futureWeatherFrag.setArguments(data);


        List<String> list = new ArrayList<>();
        List<android.support.v4.app.Fragment> fragmentList = new ArrayList<>();
        list.add(getString(R.string.today));
        list.add(getString(R.string.future));
        fragmentList.add(todayFrag);
        fragmentList.add(futureWeatherFrag);

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragmentList, list);
        viewPager.setAdapter(adapter);

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
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_setting:
                Toast.makeText(this, "还没完成...", Toast.LENGTH_LONG).show();
                break;
            case R.id.change_btn:

                alertDialog(this.getString(R.string.sure), this.getString(R.string.switch_city),
                        this.getString(R.string.no), this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(WeatherActivity.this, SelectArea.class);
                                intent.putExtra("isFromWeatherActivity", true);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_WEATHER:
                    forecastList = WeatherDB.loadForecast();
                    todayFrag.showWeather(forecastList);
                    futureWeatherFrag.refreshWeather(forecastList);

                    //WeatherActivity.setupDrawerContent();
                    Snackbar.make(WeatherActivity.fab, mcontext.getString(R.string.load_finish), Snackbar.LENGTH_LONG)
                            .show();
                    break;
                case SHOW_TEMP_DATE:
                    tempAndPushDate = WeatherDB.loadTempAndDate();
                    todayFrag.showTempAndDate(tempAndPushDate);
                    futureWeatherFrag.refreshWeather(forecastList);
                    break;

            }
        }
    };


    class PagerAdapter extends FragmentPagerAdapter {
        List<android.support.v4.app.Fragment> list;
        List<String> titleList;

        PagerAdapter(FragmentManager fm, List<android.support.v4.app.Fragment> list, List<String> titleList) {
            super(fm);
            this.list = list;
            this.titleList = titleList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return this.list.get(position);
        }


        @Override
        public int getCount() {
            return list.size();
        }
    }


}

