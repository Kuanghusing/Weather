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
import com.kuahusg.weather.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-30.
 */
public class SelectArea extends AppCompatActivity implements CityUtil.SolveCityCallback, CityUtil.QueryCityCallback {
    private AutoCompleteTextView editText;
    private static ArrayAdapter<String> arrayAdapter;
    private ListView cityListView;
    private List<String> cityList;
    private List<City> cityL;
    private ArrayAdapter<String> adapter;
    private static ProgressDialog progressDialog;
    private static List<String> cityListFromDataBase = new ArrayList<>();
    private static Context mContext;

    private Button queryButton;
    private Toolbar toolbar;
    private boolean isFromWeatherActivity;
    private WeatherDB db;


    @Override
    public void queryCityError(String message) {

        Toast.makeText(SelectArea.this, message, Toast.LENGTH_SHORT).show();
        dismissProgress();
    }

    @Override
    public void solveCity(List<String> list) {

        dismissProgress();
        hasLoadCityList(list);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SelectArea.this).edit();
        editor.putString("hasLoadCity", "OK");
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



        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        db = WeatherDB.getInstance(this);
        isFromWeatherActivity = getIntent().getBooleanExtra("isFromWeatherActivity", false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectCity = sharedPreferences.getString("selectCity", "");
        String hasLoadCity = sharedPreferences.getString("hasLoadCity", "");
        if (!isFromWeatherActivity && !TextUtils.isEmpty(selectCity)) {
            Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        queryButton = (Button) findViewById(R.id.query_button);
        editText = (AutoCompleteTextView) findViewById(R.id.city_editText);
        if (!isFromWeatherActivity) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setIcon(R.mipmap.ic_launcher);
        }
        cityList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityList);
        cityListView = (ListView) findViewById(R.id.city_list);
        cityListView.setAdapter(adapter);
        mContext = getApplicationContext();


        arrayAdapter = new ArrayAdapter<>(SelectArea.this, android.R.layout.simple_list_item_1, cityListFromDataBase);
        editText.setAdapter(arrayAdapter);


        /**
        * load the all the cities list from server
         */


        List<String> loadCity = WeatherDB.loadCity();

        if (TextUtils.isEmpty(hasLoadCity) || loadCity.size() <= 0) {
            showProgress(false);
            CityUtil.handleCityList(this, this);

        } else {
            hasLoadCityList(loadCity);

        }


        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = null;
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    showProgress(true);
                    city = editText.getText().toString();
                    CityUtil.queryCity(city, mContext, SelectArea.this);
                }
            }
        });


        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
                City city = cityL.get(position);
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


    public static void hasLoadCityList(List<String> loadCity) {

        cityListFromDataBase.clear();
        cityListFromDataBase.addAll(loadCity);


        arrayAdapter.notifyDataSetChanged();
        LogUtil.v(mContext.getClass().toString(), "cityListFrom..size()" + cityListFromDataBase.size());
    }


    @Override
    public void queryCityFinish(List<City> cityList) {
        dismissProgress();
        this.cityList.clear();
        this.cityL = cityList;
        for (City city :
                cityList) {
            this.cityList.add(city.getFullNmae());
        }
        if (this.cityList.size() > 0) {
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
