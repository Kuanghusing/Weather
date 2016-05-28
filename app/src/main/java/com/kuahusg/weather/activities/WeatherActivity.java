package com.kuahusg.weather.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    /*private static TextView date;
    private static TextView temp_now;
    private static TextView temp1;
    private static TextView temp2;
    private static TextView weather_text;
    private static TextView cal_date;*/

    private static City selectCity;
    private static String tempAndPushDate;
    private static List<Forecast> forecastList;

    private static DrawerLayout drawerLayout;
    private static NavigationView navigationView;
    public static FloatingActionButton fab;
    public  static Toolbar toolbar;
    private static TabLayout tabLayout;
    private static ViewPager viewPager;

    public static Context mcontext;
    private static int whichDay = 0;

    public static final int SHOW_WEATHER = 2;
    public static final int SHOW_TEMP_DATE = 3;
    public static Handler handler;


    /*public static RelativeLayout weather_info;
    private static LinearLayout public_layout;*/
//    public static ProgressDialog progressDialog;
    /*private static ImageView weahterPic;
    public static RelativeLayout weather_more_info;*/



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

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_paper);
        setupViewPager(viewPager,new WeatherFragment(),new FutureWeatherFrag());
        tabLayout.setupWithViewPager(viewPager);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }


//        WeatherFragment todayFrag = (WeatherFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
        PagerAdapter adapter = (PagerAdapter)viewPager.getAdapter();
        WeatherFragment todayFrag = (WeatherFragment) adapter.getItem(0);
        todayFrag.getHandle(new WeatherFragment.GetHandleCallBack() {
            @Override
            public void onResult(Handler handler) {

                setHandle(handler);
            }
        });

        /*date.setVisibility(View.INVISIBLE);
        weather_info.setVisibility(View.INVISIBLE);
        weather_more_info.setVisibility(View.INVISIBLE);
        public_layout.setVisibility(View.INVISIBLE);
        weather_info.setOnClickListener(this);*/


        Intent intent = new Intent(mcontext, AutoUpdateService.class);
        mcontext.startService(intent);


    }

    private void setHandle(Handler handle) {
        WeatherActivity.handler = handle;
    }

    /*private void InitView(View weatherFragLayout) {
        date = (TextView) weatherFragLayout.findViewById(R.id.public_data);
        temp_now = (TextView) weatherFragLayout.findViewById(R.id.temp_now);
        temp1 = (TextView) weatherFragLayout.findViewById(R.id.temp1);
        temp2 = (TextView) weatherFragLayout.findViewById(R.id.temp2);
        weather_text = (TextView) weatherFragLayout.findViewById(R.id.weather_text);
        weather_info = (RelativeLayout) weatherFragLayout.findViewById(R.id.weather_info);
        weahterPic = (ImageView) weatherFragLayout.findViewById(R.id.weather_pic);
        weather_more_info = (RelativeLayout) weatherFragLayout.findViewById(R.id.main_info);
        public_layout = (LinearLayout) weatherFragLayout.findViewById(R.id.public_layout);
        cal_date = (TextView) weatherFragLayout.findViewById(R.id.date_textview);
    }*/

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


    /*private static void showWeather(int whichDay) {
        if (!forecastList.isEmpty()) {
            Forecast forecastToday = forecastList.get(whichDay);
            String weatherText = forecastToday.getWeatherText();
            temp1.setText(forecastToday.getLow());
            temp2.setText(forecastToday.getHigh());
            weather_text.setText(weatherText);
            toolbar.setSubtitle(selectCity.getCity_name());
            cal_date.setText(forecastList.get(whichDay).getDate().substring(0, 6));

            date.setVisibility(View.VISIBLE);
            weather_info.setVisibility(View.VISIBLE);
            weather_more_info.setVisibility(View.VISIBLE);
            public_layout.setVisibility(View.VISIBLE);
            showWeatherPic(weatherText);


        } else {
            LogUtil.v(mcontext.toString(), "no size in forecastList");
        }
    }*/

    /*private static void showTempAndDate() {
        if (!TextUtils.isEmpty(tempAndPushDate)) {

            String[] t = tempAndPushDate.split("\\|");
            temp_now.setText(t[0]);
//            String date = t[1].replace(" CST", "").replaceAll(" \\d{4}", "");
            String date = t[1].substring(17, 25);
            WeatherActivity.date.setText(date);
        }
    }*/

    /*private static void showWeatherPic(String weatherText) {
        if (weatherText.contains("Thunderstorms")) {
            weahterPic.setImageResource(R.drawable.weather_thunderstorm);

        } else if (weatherText.contains("Cloudy")) {
            weahterPic.setImageResource(R.drawable.weather_cloudy);
        } else if (weatherText.contains("Sunny")) {
            weahterPic.setImageResource(R.drawable.weather_sun_day);

        } else if (weatherText.contains("Showers") || weatherText.contains("Rain")) {
            weahterPic.setImageResource(R.drawable.weather_rain);
        } else if (weatherText.contains("Breezy")) {
            weahterPic.setImageResource(R.drawable.weather_wind);
        }

    }*/


/*    private ProgressDialog showProgress() {
        if (!this.isFinishing()) {
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(WeatherActivity.this);

            }
            LogUtil.v(this.toString(), WeatherActivity.this.toString());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setTitle("");
            progressDialog.show();
        }


//        progressDialog.show();
        return progressDialog;
    }

    public static void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.weather_info:
                queryWeatherFromServer(selectCity);
                toolbar.setSubtitle(R.string.loading);
                break;*/
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


    public static void setupDrawerContent() {
        /*String date;
        List<Integer> idList = new ArrayList<>();
        idList.add(R.id.day1);
        idList.add(R.id.day2);
        idList.add(R.id.day3);
        idList.add(R.id.day4);
        idList.add(R.id.day5);
        for (int i = 2; i < 5; i++) {
            date = forecastList.get(i).getDate().substring(0, 6);
            navigationView.getMenu().findItem(idList.get(i)).setTitle(date);

        }*/

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
                    /*case R.id.day1:
                        whichDay = 0;

                        break;
                    case R.id.day2:
                        whichDay = 1;
                        break;
                    case R.id.day3:
                        whichDay = 2;
                        break;
                    case R.id.day4:
                        whichDay = 3;
                        break;
                    case R.id.day5:
                        whichDay = 4;
                        break;*/
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


//                showWeather(whichDay);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                    drawerLayout.closeDrawers();
                }

                return false;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager,WeatherFragment todayFrag,FutureWeatherFrag futureWeatherFrag) {


        Bundle data = new Bundle();
        data.putParcelableArrayList("forecastList",(ArrayList<Forecast>)forecastList);
        data.putString("tempAndPushDate",tempAndPushDate);
        data.putSerializable("selectCity",selectCity);
        todayFrag.setArguments(data);
        futureWeatherFrag.setArguments(data);


        List<String> list = new ArrayList<>();
        List<android.support.v4.app.Fragment> fragmentList = new ArrayList<>();
        list.add(getString(R.string.today));
        list.add(getString(R.string.future));
        fragmentList.add(todayFrag);
        fragmentList.add(futureWeatherFrag);

        FragmentPagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),fragmentList,list);
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








    class PagerAdapter extends FragmentPagerAdapter {
        List<android.support.v4.app.Fragment> list;
        List<String> titleList;

        public PagerAdapter(FragmentManager fm, List<android.support.v4.app.Fragment> list, List<String> titleList) {
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

