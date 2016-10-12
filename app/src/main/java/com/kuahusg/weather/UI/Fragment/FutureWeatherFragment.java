package com.kuahusg.weather.UI.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuahusg.weather.Presenter.FutureWeatherFragPresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IFutureWeatherFragPresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IWeatherViewPresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.Adapter.RvAdapter;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.base.BaseFragment;
import com.kuahusg.weather.UI.interfaceOfView.IFutureWeatherFragView;
import com.kuahusg.weather.model.bean.Forecast;

import java.util.List;

/**
 * Created by kuahusg on 16-9-29.
 */

public class FutureWeatherFragment extends BaseFragment implements IFutureWeatherFragView, View.OnClickListener {


    private View view;
    private RecyclerView mRvForecastList;
    private LinearLayout mLayoutMessage;
    private TextView mTvMessage;

    private RvAdapter mRvAdapter;

    private IFutureWeatherFragPresenter mPresenter;

    private boolean messageShowed = false;


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_future;
    }

    @Override
    protected IBasePresenter setPresenter() {
        mPresenter = new FutureWeatherFragPresenterImpl(this);
        return mPresenter;

    }

    @Override
    public void init() {
        showMessage(true, getString(R.string.loading));
    }

    @Override
    public void start() {

    }

    @Override
    public void error(String message) {
        showMessage(true, message);
    }

    @Override
    public void finish() {

    }

    @Override
    public void showForecast(List<Forecast> forecastList) {
        showMessage(false, null);
        messageShowed = true;
        if (mRvAdapter != null) {
            mRvAdapter.setData(forecastList);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        initView();
        if (hasPresenter())
            mPresenter.init();
        return view;
    }

    @Override
    public void scrollToTop() {
        if (mRvForecastList != null) {
            mRvForecastList.smoothScrollToPosition(0);

        }
    }

    private void initView() {

        mTvMessage = (TextView) view.findViewById(R.id.tv_message);
        mTvMessage.setText(getString(R.string.loading));
        mLayoutMessage = (LinearLayout) view.findViewById(R.id.layout_message);

        mRvAdapter = new RvAdapter(null, getContext());
        mRvForecastList = (RecyclerView) view.findViewById(R.id.rv_forecast_info_list);
        mRvForecastList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvForecastList.setItemAnimator(new DefaultItemAnimator());
        mRvForecastList.setHasFixedSize(true);
        mRvForecastList.setAdapter(mRvAdapter);
        mLayoutMessage.setOnClickListener(this);

    }

    private void showMessage(boolean show, String message) {
        if (messageShowed)
            return;
        if (!this.isResumed())
            return;
        if (!show && mLayoutMessage.getVisibility() == View.VISIBLE) {
            mLayoutMessage.setVisibility(View.GONE);
        } else {
            mLayoutMessage.setVisibility(View.VISIBLE);
            mTvMessage.setText(message);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_message:
                BaseActivity view1 = (BaseActivity) getActivity();
                IWeatherViewPresenter presenter = null;
                presenter = (IWeatherViewPresenter) view1.getPresenter();
                presenter.refreshWeather();
                break;
        }
    }
}



