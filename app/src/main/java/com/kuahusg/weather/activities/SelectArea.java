package com.kuahusg.weather.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kuahusg.weather.R;
import com.kuahusg.weather.db.WeatherDB;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.util.LogUtil;
import com.kuahusg.weather.util.Utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-4-30.
 */
public class SelectArea extends AppCompatActivity {
    private Button queryButton;
    private EditText editText;
    private ListView cityListView;
    private List<String> cityList;
    private List<City> cityL;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);
        queryButton = (Button) findViewById(R.id.query_button);

        editText = (EditText) findViewById(R.id.city_editText);
        cityListView = (ListView) findViewById(R.id.city_list);
        cityList = new ArrayList<>();

        adapter = new ArrayAdapter<>(SelectArea.this, android.R.layout.simple_list_item_1, cityList);
        cityListView.setAdapter(adapter);

        WeatherDB db = WeatherDB.getInstance(this);

//        progressDialog = new ProgressDialog(this);
        isFromWeatherActivity = getIntent().getBooleanExtra("isFromWeatherActivity", false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectCity = sharedPreferences.getString("selectCity", "");
        if (!isFromWeatherActivity && !TextUtils.isEmpty(selectCity)) {
            Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        queryButton.setOnClickListener(new View.OnClickListener() {

            @Override


            public void onClick(View v) {
                showProgress();

//                cityList.clear();
                String city = editText.getText().toString();
                    Utility.quaryCity(city);

                cityL = Utility.cityList;

//                LogUtil.v(this.toString() + "\tcityL.size()", cityL.size() + "\t");
                cityList.clear();
                for (City c :
                        cityL) {
                    cityList.add(c.getFullNmae());
                }
/*                LogUtil.v(this.getClass().getName() + "\tcityList.size()", +cityList.size() + "\t");
                LogUtil.v(this.getClass().getName() + "\tcityList(0)", cityList.get(0));*/

                if (cityList.size() > 0) {
                    adapter.notifyDataSetChanged();
                    cityListView.setSelection(0);

                } else {
                    Toast.makeText(SelectArea.this,"it seem no result...try again",Toast.LENGTH_LONG).show();
                }
                dismissProgress();

            }
        });


        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectArea.this, WeatherActivity.class);
                City city = cityL.get(position);
                if (city != null) {
                    intent.putExtra("selectCity", city);
                    startActivity(intent);
                    finish();

                }

            }
        });


    }

    public void showProgress() {
            progressDialog = ProgressDialog.show(SelectArea.this, "loading", null);
    }

    public void dismissProgress() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
