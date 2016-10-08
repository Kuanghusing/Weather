package com.kuahusg.weather.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.kuahusg.weather.Presenter.base.BasePresenter;
import com.kuahusg.weather.Presenter.interfaceOfPresenter.IAboutMePresenter;
import com.kuahusg.weather.R;
import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.UI.interfaceOfView.IAboutMeView;
import com.kuahusg.weather.data.IDataSource;

/**
 * Created by kuahusg on 16-9-27.
 */

public class AboutMePresenterImpl extends BasePresenter implements IAboutMePresenter {
    private IAboutMeView mView;

    public AboutMePresenterImpl(IBaseView view) {
        super(view);
        mView = (IAboutMeView) view;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected IDataSource setDataSource() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void onClickFab(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(activity.getString(R.string.open_source_text)));
        activity.startActivity(intent);

    }
}
