package com.kuahusg.weather.UI.interfaceOfView;

import com.kuahusg.weather.UI.base.IBaseView;

/**
 * Created by kuahusg on 16-9-27.
 */

public interface IAboutMeView extends IBaseView {
    @Override
    void init();

    @Override
    void start();

    @Override
    void finish();

    @Override
    void error(String message);


}
