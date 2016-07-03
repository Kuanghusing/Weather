package com.kuahusg.weather.UI.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.kuahusg.weather.R;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.db.WeatherDB;
import com.kuahusg.weather.util.CityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-30.
 */
public class SelectArea extends AppCompatActivity implements CityUtil.SolveCityCallback, CityUtil.QueryCityCallback {
    private AutoCompleteTextView editText;
    private static ArrayAdapter<String> autoCompleteTextAdapter;
    private ListView cityListView;
    private List<String> cityNameList;
    private List<City> searchResultCityList;
    private ArrayAdapter<String> adapter;
    private static ProgressDialog progressDialog;
    private static List<String> cityListFromDataBase = new ArrayList<>();
    private static Context mContext;

    private Button queryButton;
    private Toolbar toolbar;
    private boolean isFromWeatherActivity;
    private WeatherDB db;
    private String selectCity;
    private boolean hasLoadAllCity;


    @Override
    public void queryCityError(String message) {

        Toast.makeText(SelectArea.this, message, Toast.LENGTH_SHORT).show();
        dismissProgress();
    }

    @Override
    public void solveCity(List<String> list) {

        dismissProgress();
        allCityLoadFinish(list);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SelectArea.this).edit();
        editor.putBoolean("hasLoadAllCity", true);
        editor.apply();
    }

    @Override
    public void solveCityError(String message) {

        Snackbar.make(cityListView, message, Snackbar.LENGTH_LONG).show();
        dismissProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);

        isFromWeatherActivity = getIntent().getBooleanExtra("isFromWeatherActivity", false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectCity = sharedPreferences.getString("selectCity", "");
        hasLoadAllCity = sharedPreferences.getBoolean("hasLoadAllCity", false);


        if (!isFromWeatherActivity && !TextUtils.isEmpty(selectCity)) {
            Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initView();
    }


    private void initView() {
        db = WeatherDB.getInstance(this);
        mContext = getApplicationContext();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        queryButton = (Button) findViewById(R.id.query_button);
        editText = (AutoCompleteTextView) findViewById(R.id.city_editText);
        cityListView = (ListView) findViewById(R.id.city_list);

        if (!isFromWeatherActivity && actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setIcon(R.mipmap.ic_launcher);
        }


        cityNameList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNameList);
        if (cityListView != null) {
            cityListView.setAdapter(adapter);
        }


        autoCompleteTextAdapter = new ArrayAdapter<>(SelectArea.this, android.R.layout.simple_list_item_1, cityListFromDataBase);
        editText.setAdapter(autoCompleteTextAdapter);


        /**
         * load the all the cities list from server
         */


        List<String> loadCity = WeatherDB.loadCity();

        if (!hasLoadAllCity || loadCity.size() <= 0) {
            showProgress(false);
            CityUtil.handleCityList(this, this);
        } else {
            allCityLoadFinish(loadCity);

        }


        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName;
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    showProgress(true);
                    cityName = editText.getText().toString();
                    CityUtil.queryCity(cityName, mContext, SelectArea.this);
                }
            }
        });


        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
                City city = searchResultCityList.get(position);
                if (city != null) {
                    intent.putExtra("selectCity", city);
                    if (isFromWeatherActivity)
                        intent.putExtra("anotherCity", true);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }

    public static void allCityLoadFinish(List<String> loadCity) {

        cityListFromDataBase.clear();
        cityListFromDataBase.addAll(loadCity);
        autoCompleteTextAdapter.notifyDataSetChanged();
    }


    @Override
    public void queryCityFinish(List<City> cityList) {
        dismissProgress();
        this.cityNameList.clear();
        this.searchResultCityList = cityList;
        for (City city :
                cityList) {
            this.cityNameList.add(city.getFullNmae());
        }
        if (this.cityNameList.size() > 0) {
            adapter.notifyDataSetChanged();
            cityListView.setSelection(0);
        }
    }


    public static void dismissProgress() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    public void showProgress(boolean cancelable) {
        if (!this.isFinishing()) {
            if (progressDialog == null)
                progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(cancelable);
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFromWeatherActivity) {
            Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();

    }
}
