package com.kuahusg.weather.UI.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.Fragment.SettingFragment;

/**
 * Created by kuahusg on 16-6-10.
 * com.kuahusg.weather.UI.activities
 */
public class SettingActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_setting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

/*
        ViewGroup viewRoot = (ViewGroup) findViewById(android.R.id.content);
        View content = viewRoot.getChildAt(0);

        LinearLayout toolbarLayout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.activity_setting, null);
        viewRoot.removeAllViews();
        toolbarLayout.addView(content);
        viewRoot.addView(toolbarLayout);*/

        getFragmentManager().beginTransaction().replace(R.id.setting_part, new SettingFragment()).commit();


    }


}
