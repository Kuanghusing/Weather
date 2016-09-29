package com.kuahusg.weather.UI.interfaceOfView;

import com.kuahusg.weather.UI.base.IBaseView;
import com.kuahusg.weather.model.City;

import java.util.List;

/**
 * Created by kuahusg on 16-9-27.
 */

public interface ISelectLocationView extends IBaseView {

    @Override
    void start();

    @Override
    void finish();

    @Override
    void init();

    @Override
    void error(String message);

    void queryCityError(String message);

    void finishQueryCity(List<City> list);

    void loadAllCityFinish(List<String> cityList);


}
