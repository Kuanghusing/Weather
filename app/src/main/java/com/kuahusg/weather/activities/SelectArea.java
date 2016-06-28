package com.kuahusg.weather.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-30.
 */
public class SelectArea extends AppCompatActivity {
    public static final int PROSSDIALOG_DISSMISS = 1;
    public static final int RESULT_OK = 2;
    public static final int UPDATE_CITY_LIST = 3;
    public static AutoCompleteTextView editText;
    public static ArrayAdapter<String> arrayAdapter;
    public static ListView cityListView;
    public static List<String> cityList;
    public static List<City> cityL;
    public static ArrayAdapter<String> adapter;
    private static ProgressDialog progressDialog;
    private static List<String> cityListFromDataBase = new ArrayList<>();
    private static Context mContext;
    /*    public static Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PROSSDIALOG_DISSMISS:
                        dismissProgress();
                        break;
                    case RESULT_OK:

                        cityL = (List<City>) msg.obj;

                        SelectArea.cityList.clear();
                        for (City c :
                                cityL) {
                            SelectArea.cityList.add(c.getFullNmae());
                        }

                        if (SelectArea.cityList.size() > 0) {
                            adapter.notifyDataSetChanged();
                            cityListView.setSelection(0);

                        }
                        break;
                    case UPDATE_CITY_LIST:

                        List<String> loadCity = WeatherDB.loadCity();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                        editor.putString("hasLoadCity", "OK");
                        editor.apply();
                        hasLoadCityList(loadCity);

                }
            }
        };*/
    //    private ImageView queryButton;
    private Button queryButton;
    private Toolbar toolbar;
    private boolean isFromWeatherActivity;
    private WeatherDB db;

    public static void hasLoadCityList(List<String> loadCity) {

        cityListFromDataBase.clear();
        cityListFromDataBase.addAll(loadCity);


        arrayAdapter.notifyDataSetChanged();
        LogUtil.v(mContext.getClass().toString(), "cityListFrom..size()" + cityListFromDataBase.size());
    }

    public static void dismissProgress() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);
//        View view = getWindow().getDecorView();

         /*
        * from WeatherActivity? go to WeatherAcitvity directly
         */

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
//        actionBar = getSupportActionBar();
        if (!isFromWeatherActivity) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setIcon(R.mipmap.ic_launcher);
        }
        //        actionBar.setDisplayHomeAsUpEnabled(true);
        cityList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityList);
        cityListView = (ListView) findViewById(R.id.city_list);
        cityListView.setAdapter(adapter);
        mContext = getApplicationContext();


        arrayAdapter = new ArrayAdapter<>(SelectArea.this, android.R.layout.simple_list_item_1, cityListFromDataBase);
        editText.setAdapter(arrayAdapter);





        /*
        * load the all the cities list from server
         */

        List<String> loadCity = WeatherDB.loadCity();

        if (TextUtils.isEmpty(hasLoadCity) || loadCity.size() <= 0) {
            showProgress(false);
            Utility.handleCityList();

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
                    Utility.queryCity(city, mContext);
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

    public void showProgress(boolean cancelable) {
        if (!isFinishing()) {
            if (progressDialog == null)
                progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(cancelable);
            progressDialog.show();
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
