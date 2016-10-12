package com.kuahusg.weather.UI.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.kuahusg.weather.Presenter.SelectLocationPresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.ISelectLocationPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.interfaceOfView.ISelectLocationView;
import com.kuahusg.weather.model.bean.City;
import com.kuahusg.weather.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-9-27.
 */

public class SelectLocationActivity extends BaseActivity implements ISelectLocationView, View.OnClickListener, ListView.OnItemClickListener {
    private ISelectLocationPresenter mPresenter;

    private AppCompatAutoCompleteTextView mAutoCpTv;
    private FrameLayout mLayoutMain;
    private ArrayAdapter<String> mAutoCpTvAdapter;
    private ListView mLvCitySearchResult;
    private ArrayAdapter<String> mCitySearchResultAdapter;
    private ProgressDialog mPgd;
    private Button mBtnSearch;
    private Toolbar mToolbar;
    private CardView mCardViewMain;

    private List<String> searchResultNameOfCitys;
    private List<City> searchResultCityList;
    private List<String> cityListFromDataBase;


    @Override
    protected IBasePresenter setPresenter() {
        this.mPresenter = new SelectLocationPresenterImpl(this);
        return mPresenter;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_select_location;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        if (hasPresenter())
            mPresenter.init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimation(mCardViewMain, R.anim.anim_weather_main);
        Log.d(this.getClass().getSimpleName(), "count:" + mAutoCpTvAdapter.getCount());
        if (hasPresenter())
            if (mAutoCpTvAdapter != null && mAutoCpTvAdapter.getCount() == 0)
                mPresenter.start();
    }

    @Override
    public void queryCityError(String message) {
        dismissProgress();
        mLvCitySearchResult.setVisibility(View.INVISIBLE);
        Snackbar.make(mLayoutMain, message, Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hasPresenter())
                            mPresenter.onClickQueryButton(mAutoCpTv.getText().toString());

                    }
                })
                .show();
    }

    @Override
    public void finishQueryCity(List<City> list) {
        dismissProgress();
        mLvCitySearchResult.setVisibility(View.VISIBLE);
        startAnimation(mLvCitySearchResult, R.anim.anim_alpha_in);
        this.searchResultCityList = list;
        searchResultNameOfCitys.clear();
        for (City city :
                list) {
            searchResultNameOfCitys.add(city.getFullNmae());
        }
        mCitySearchResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadAllCityFinish(List<String> cityList) {
        LogUtil.d(this.getClass().getSimpleName(), "cityList count:" + cityList.size());
        dismissProgress();
        cityListFromDataBase.clear();
        cityListFromDataBase.addAll(cityList);
        mAutoCpTvAdapter.notifyDataSetChanged();
        // TODO: 16-10-12 为什么突然不行了...

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void startLoadingData(boolean canCancelPgb) {

        showProgress(canCancelPgb);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.query_button) {
            if (!TextUtils.isEmpty(mAutoCpTv.getText())) {
                showProgress(true);
                if (hasPresenter())
                    mPresenter.onClickQueryButton(mAutoCpTv.getText().toString());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (hasPresenter())
            mPresenter.onClickResultCityItem(searchResultCityList.get(i), SelectLocationActivity.this);

    }

    private void initView() {
        cityListFromDataBase = new ArrayList<>();
        searchResultNameOfCitys = new ArrayList<>();
        searchResultCityList = new ArrayList<>();

/*        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setIcon(R.mipmap.ic_launcher);*/

        mBtnSearch = (Button) findViewById(R.id.query_button);
        mBtnSearch.setOnClickListener(this);
        mAutoCpTv = (AppCompatAutoCompleteTextView) findViewById(R.id.city_editText);
        mLvCitySearchResult = (ListView) findViewById(R.id.city_list);
        mLvCitySearchResult.setOnItemClickListener(this);
        mLayoutMain = (FrameLayout) findViewById(R.id.layout_main);
        mCardViewMain = (CardView) findViewById(R.id.cardView_main);

        mCitySearchResultAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResultNameOfCitys);
        mLvCitySearchResult.setAdapter(mCitySearchResultAdapter);
//        mLvCitySearchResult.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));

        mAutoCpTvAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityListFromDataBase);
        mAutoCpTv.setAdapter(mAutoCpTvAdapter);
    }


    private void showProgress(boolean cancelable) {

        if (!this.isFinishing()) {
            if (mPgd == null)
                mPgd = new ProgressDialog(this);
            mPgd.setMessage(getString(R.string.loading));
            mPgd.setCancelable(cancelable);
            // TODO: 16-9-29 ??
//            try {
            mPgd.show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    private void dismissProgress() {
        if (mPgd != null && mPgd.isShowing())
            mPgd.dismiss();
    }

    private void startAnimation(View view, int animId) {
        Animation animation = AnimationUtils.loadAnimation(this, animId);
        view.startAnimation(animation);

    }
}
