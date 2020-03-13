package com.example.thweather;

import android.os.Parcel;
import android.os.Parcelable;

public class Weather implements Parcelable {

    private int wImageId;   //图片的id
    private String wName;   //天气名称
    private String dateName;//日期
    private String weekday; //周几
    private String hDegree; //最高温
    private String lDegree; //最低温
    private String humidity;//湿度
    private String pressure;//气压
    private String wind;    //风速

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public int getwImageId() {
        return wImageId;
    }

    public void setwImageId(int wImageId) {
        this.wImageId = wImageId;
    }

    public String getwName() {
        return wName;
    }

    public void setwName(String wName) {
        this.wName = wName;
    }

    public String getDateName() {
        return dateName;
    }

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    public String gethDegree() {
        return hDegree;
    }

    public void sethDegree(String hDegree) {
        this.hDegree = hDegree;
    }

    public String getlDegree() {
        return lDegree;
    }

    public void setlDegree(String lDegree) {
        this.lDegree = lDegree;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public Weather() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.wImageId);
        parcel.writeString(this.wName);
        parcel.writeString(this.dateName);
        parcel.writeString(this.hDegree);
        parcel.writeString(this.lDegree);
        parcel.writeString(this.humidity);
        parcel.writeString(this.pressure);
        parcel.writeString(this.wind);
        parcel.writeString(this.weekday);
    }

    protected Weather(Parcel parcel) {
        this.wImageId = parcel.readInt();
        this.wName = parcel.readString();
        this.dateName = parcel.readString();
        this.hDegree = parcel.readString();
        this.lDegree = parcel.readString();
        this.humidity = parcel.readString();
        this.pressure = parcel.readString();
        this.wind = parcel.readString();
        this.weekday = parcel.readString();
    }

    public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel parcel) {
            return new Weather(parcel);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };
}

class WeatherNow {
    String tmp;     //温度
    String cond;    //天气状况

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public WeatherNow() {
    }
}
