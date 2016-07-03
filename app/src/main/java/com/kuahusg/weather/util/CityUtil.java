package com.kuahusg.weather.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuahusg.weather.R;
import com.kuahusg.weather.model.City;
import com.kuahusg.weather.model.Data.CitySearchResult;
import com.kuahusg.weather.model.Data.Citys;
import com.kuahusg.weather.model.db.WeatherDB;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuahusg on 16-7-3.
 */
public class CityUtil {
    private static Context mContext;
    private static WeatherDB db;
    public static final int ERROR_SOLVE_CITY = 1;
    public static final int ERROR_QUERY_CITY = 2;
    private static SolveCityCallback solveCityCallback;
    private static QueryCityCallback queryCityCallback;


    public static void queryCity(String city_name, final Context context, QueryCityCallback queryCityCallback) {
        mContext = context;
        db = WeatherDB.getInstance(context);
        CityUtil.queryCityCallback = queryCityCallback;

        try {
            city_name = URLEncoder.encode(city_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String yql = " select woeid,name,country.content," +
                "admin1.content,admin2.content,admin3.content from geo.places(1) " +
                "where text=\"" + city_name + "\" and lang = \"zh-CN\" &format=json";
        final String new_address = "https://query.yahooapis.com/v1/public/yql?q=" +
                yql.replaceAll(" ", "%20").replaceAll("\"", "%22");
        new QueryCityTask().execute(new_address);

    }

    public static void handleCityList(SolveCityCallback solveCityCallback, Context context) {
        mContext = context;
        CityUtil.solveCityCallback = solveCityCallback;
        if (db == null && mContext != null) {
            db = WeatherDB.getInstance(mContext);
        }

        WeatherDB.deleteTable("city");
        String address = "https://raw.githubusercontent.com/Kuanghusing/City_list/master/city-list";

        new HandleCityListTask().execute(address);


    }


    private static class QueryCityTask extends AsyncTask<String, String, List<City>> {
        @Override
        protected List<City> doInBackground(String... params) {
            String result = "";
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");


            } catch (Exception e) {
                if (mContext != null) {
                    Message message = new Message();
                    message.what = ERROR_QUERY_CITY;
                    message.obj = mContext.getString(R.string.no_network);
                    handler.sendMessage(message);
                    e.printStackTrace();
                    cancel(true);
                }
            }
            List<City> cityList = new ArrayList<>();
            City city;
            Gson gson = new Gson();
            CitySearchResult citySearchResult = gson.fromJson(result, CitySearchResult.class);
            StringBuilder fullName;


            if (!TextUtils.isEmpty(result)) {

                String woeid = null;
                String name = null;
                String country = null;
                String admin1 = null;
                String admin2 = null;
                String admin3 = null;
                try {
                    woeid = citySearchResult.getQuery().getResults().getPlace().getWoeid();
                    name = citySearchResult.getQuery().getResults().getPlace().getName();
                    country = citySearchResult.getQuery().getResults().getPlace().getCountry();
                    admin1 = citySearchResult.getQuery().getResults().getPlace().getAdmin1();
                    admin2 = citySearchResult.getQuery().getResults().getPlace().getAdmin2();
                    admin3 = citySearchResult.getQuery().getResults().getPlace().getAdmin3();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = ERROR_QUERY_CITY;
                    message.obj = mContext.getString(R.string.no_result);
                    handler.sendMessage(message);
                }
                fullName = new StringBuilder();


                if (country != null) {
                    fullName.append(country);
                }
                if (admin1 != null) {
                    fullName.append(admin1);
                }
                if (admin2 != null) {
                    fullName.append(admin2);
                }
                if (admin3 != null) {
                    fullName.append(admin3);
                }
                city = new City(name, woeid, fullName.toString());
                LogUtil.v("WeatherUtil.queryCity:city", city.getCity_name() + city.getFullNmae() +
                        city.getWoeid());
                cityList.add(city);
                LogUtil.v(this.getClass().getName() + "\tcityList.size()", cityList.size() + "\t");


            }
            return cityList;
        }


        @Override
        protected void onPostExecute(List<City> cityList) {
            super.onPostExecute(cityList);
            queryCityCallback.queryCityFinish(cityList);

        }

    }

    private static class HandleCityListTask extends AsyncTask<String, String, List<Citys>> {
        @Override
        protected List<Citys> doInBackground(String... params) {
            String result = null;
            try {
                result = HttpUtil.sendHttpReauest(params[0], "GET");

            } catch (Exception e) {
                Message message = new Message();
                message.what = ERROR_SOLVE_CITY;
                message.obj = mContext.getString(R.string.no_network);
                handler.sendMessage(message);
                cancel(true);
            }

            List<Citys> list = new ArrayList<>();

            Gson gson;
            try {
                gson = new Gson();
                list = gson.fromJson(result, new TypeToken<List<Citys>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = ERROR_QUERY_CITY;
                message.obj = mContext.getString(R.string.no_result);
                handler.sendMessage(message);
                cancel(true);
            }

            return list;
        }


        @Override
        protected void onPostExecute(List<Citys> cityList) {
            super.onPostExecute(cityList);
            List<String> list = new ArrayList<>();
            if (cityList != null) {
                for (int i = 0; i < cityList.size(); i++) {
                    Citys citys = cityList.get(i);
                    String name = citys.getName();
                    String parent1 = citys.getParent1();
                    String parent2 = citys.getParent2();
                    String parent3 = citys.getParent3();
                    StringBuilder stringInfo = new StringBuilder();

                    if (!(parent3.equals("直辖市") || parent2.equals(parent3))) {
                        stringInfo.append(parent3).append(" ").append(parent2).
                                append(" ").append(parent1).append(name);

                    } else {
                        stringInfo.append(parent1).append(name);
                    }
                    list.add(stringInfo.toString());
                    stringInfo.setLength(0);
                }


                /**
                 * save to database
                 */
                WeatherDB.saveCity(list);

                solveCityCallback.solveCity(list);
            }

        }
    }


    public interface SolveCityCallback {
        void solveCity(List<String> list);

        void solveCityError(String message);
    }

    public interface QueryCityCallback {
        void queryCityFinish(List<City> cityList);

        void queryCityError(String message);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ERROR_SOLVE_CITY:
                    solveCityCallback.solveCityError(((String) msg.obj));
                    break;
                case ERROR_QUERY_CITY:
                    queryCityCallback.queryCityError((String) msg.obj);
                    break;

            }
        }
    };
}
