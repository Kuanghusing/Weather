package com.kuahusg.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kuahusg on 16-5-1.
 */
public class Forecast implements Parcelable{
    public static final Parcelable.Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel source) {
            Forecast forecast = new Forecast(source.readString(), source.readString(), source.readString(),
                    source.readString(), source.readString());

            return null;
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };
    private String date;
    private String high;
    private String low;
    private String text;
    private String woeid;

    public Forecast(String date, String high, String low, String text, String woeid) {
        this.date = date;
        this.high = high;
        this.low = low;
        this.text = text;
        this.woeid = woeid;
    }



    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getText() {
        return text;
    }

    public String getWoeid() {
        return woeid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(date);
        dest.writeString(high);
        dest.writeString(low);
        dest.writeString(text);
        dest.writeString(woeid);
    }
}
