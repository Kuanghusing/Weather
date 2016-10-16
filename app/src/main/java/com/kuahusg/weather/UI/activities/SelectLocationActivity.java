package com.kuahusg.weather.UI.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

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

    private AutoCompleteTextView mAutoCpTv;
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
        // TODO: 16-10-13  
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
        if (dismissProgress())
            startAnimation(mCardViewMain, R.anim.anim_weather_main);
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


        mBtnSearch = (Button) findViewById(R.id.query_button);
        mBtnSearch.setOnClickListener(this);
        mAutoCpTv = (AutoCompleteTextView) findViewById(R.id.city_editText);
        mLvCitySearchResult = (ListView) findViewById(R.id.city_list);
        mLvCitySearchResult.setOnItemClickListener(this);
        mLayoutMain = (FrameLayout) findViewById(R.id.layout_main);
        mCardViewMain = (CardView) findViewById(R.id.cardView_main);

        mCitySearchResultAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResultNameOfCitys);
        mLvCitySearchResult.setAdapter(mCitySearchResultAdapter);
//        mLvCitySearchResult.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));

        mAutoCpTvAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityListFromDataBase);
        mAutoCpTv.setAdapter(mAutoCpTvAdapter);
        mAutoCpTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                showProgress(true);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mAutoCpTv.getWindowToken(), 0);
//                mAutoCpTv.clearFocus();
                if (hasPresenter())
                    mPresenter.onClickQueryButton(mAutoCpTv.getText().toString());
                return true;
            }
        });
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

    private boolean dismissProgress() {
        if (mPgd != null && mPgd.isShowing()) {
            mPgd.dismiss();
            return true;
        } else
            return false;
    }

    private void startAnimation(View view, int animId) {
        Animation animation = AnimationUtils.loadAnimation(this, animId);
        view.startAnimation(animation);

    }
}
