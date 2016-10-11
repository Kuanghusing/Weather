package com.kuahusg.weather.UI.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.kuahusg.weather.Presenter.SelectLocationPresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.ISelectLocationPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.interfaceOfView.ISelectLocationView;
import com.kuahusg.weather.model.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-9-27.
 */

public class SelectLocationActivity extends BaseActivity implements ISelectLocationView, View.OnClickListener, ListView.OnItemClickListener {
    private ISelectLocationPresenter mPresenter;

    private AutoCompleteTextView mAutoCpTv;
    private ArrayAdapter<String> mAutoCpTvAdapter;
    private ListView mLvCitySearchResult;
    private ArrayAdapter<String> mCitySearchResultAdapter;
    private ProgressDialog mPgd;
    private Button mBtnSearch;
    private Toolbar mToolbar;

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
        if (hasPresenter())
            mPresenter.start();
    }

    @Override
    public void queryCityError(String message) {
        Snackbar.make(mBtnSearch, message, Snackbar.LENGTH_SHORT)
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
        dismissProgress();
        cityListFromDataBase.clear();
        cityListFromDataBase.addAll(cityList);
        mAutoCpTvAdapter.notifyDataSetChanged();

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
            showProgress(true);
            if (hasPresenter())
                mPresenter.onClickQueryButton(mAutoCpTv.getText().toString());

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setIcon(R.mipmap.ic_launcher);

        mBtnSearch = (Button) findViewById(R.id.query_button);
        mBtnSearch.setOnClickListener(this);
        mAutoCpTv = (AutoCompleteTextView) findViewById(R.id.city_editText);
        mLvCitySearchResult = (ListView) findViewById(R.id.city_list);
        mLvCitySearchResult.setOnItemClickListener(this);

        mCitySearchResultAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResultNameOfCitys);
        mLvCitySearchResult.setAdapter(mCitySearchResultAdapter);

        mAutoCpTvAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityListFromDataBase);
        mAutoCpTv.setAdapter(mAutoCpTvAdapter);
    }


    private void showProgress(boolean cancelable) {

        if (!this.isFinishing()) {
            if (mPgd == null)
                mPgd = new ProgressDialog(this);
            mPgd.setMessage("loading");
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
}
