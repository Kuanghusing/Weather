package com.kuahusg.weather.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kuahusg.weather.Fragment.SettingFrag;
import com.kuahusg.weather.R;

/**
 * Created by kuahusg on 16-6-10.
 * com.kuahusg.weather.activities
 */
public class Setting extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preference);
        setContentView(R.layout.setting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

/*
        ViewGroup viewRoot = (ViewGroup) findViewById(android.R.id.content);
        View content = viewRoot.getChildAt(0);

        LinearLayout toolbarLayout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.setting, null);
        viewRoot.removeAllViews();
        toolbarLayout.addView(content);
        viewRoot.addView(toolbarLayout);*/

        getFragmentManager().beginTransaction().replace(R.id.setting_part, new SettingFrag()).commit();


    }


}
