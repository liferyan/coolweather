package com.liferyan.coolweather.model;

/**
 * Created by Ryan on 16/9/29.
 */

//城市基本信息
public class Basic {
    //城市名称
    private String city;
    //数据更新当地时间
    private String time;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time + "更新";
    }
}
