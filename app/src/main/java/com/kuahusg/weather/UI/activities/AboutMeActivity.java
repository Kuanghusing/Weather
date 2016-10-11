package com.kuahusg.weather.UI.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kuahusg.weather.Presenter.AboutMePresenterImpl;
import com.kuahusg.weather.Presenter.base.IBasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IAboutMePresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.base.BaseActivity;
import com.kuahusg.weather.UI.interfaceOfView.IAboutMeView;

/**
 * Created by kuahusg on 16-9-27.
 */

public class AboutMeActivity extends BaseActivity implements IAboutMeView {
    private IAboutMePresenter mPresenter;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPresenter())
                    mPresenter.onClickFab(AboutMeActivity.this);

            }
        });
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_layout);
        mCollapsingToolbarLayout.setTitle(getString(R.string.about));
    }

    @Override
    protected IBasePresenter setPresenter() {
        mPresenter = new AboutMePresenterImpl(this);
        return mPresenter;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void init() {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void start() {

    }
}
