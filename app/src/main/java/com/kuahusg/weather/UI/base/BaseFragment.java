package com.kuahusg.weather.UI.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuahusg.weather.Presenter.base.IBasePresenter;

/**
 * Created by kuahusg on 16-9-29.
 */

public abstract class BaseFragment extends Fragment implements IBaseView {

    private IBasePresenter mPresenter;

    protected abstract int setLayoutId();


    public IBasePresenter getPresenter() {
        return this.mPresenter;
    }


    public boolean hasPresenter() {
        return this.mPresenter != null;
    }

    protected abstract IBasePresenter setPresenter();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = setPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(setLayoutId(), container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
/*        RefWatcher watcher = App.getWatcher(getActivity());
        watcher.watch(this);*/

        if (hasPresenter())
            mPresenter.onDestroy();
    }
}
