package com.kuahusg.weather.UI.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kuahusg.weather.Presenter.base.IBasePresenter;

/**
 * Created by kuahusg on 16-9-27.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {
    private IBasePresenter mPresenter;


    public IBasePresenter getPresenter() {
        if (mPresenter != null) {
            return mPresenter;
        }

        return null;
    }


    public boolean hasPresenter() {
        return mPresenter != null;
    }


    protected abstract IBasePresenter setPresenter();

    protected abstract int setLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = setPresenter();

        setContentView(setLayoutId());


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getSimpleName(), "Activity onDestroy");
//        RefWatcher watcher = App.getWatcher(this);
//        watcher.watch(this);
        if (hasPresenter())
            mPresenter.onDestroy();

    }
}
