package com.liferyan.coolweather.model;

/**
 * Created by Ryan on 16/9/29.
 */

//实况天气
public class Now {
    //当前温度
    private String tmp;
    //天气状况
    private String cond;
    //风向
    private String dir;
    //风速
    private String sc;

    public String getTmp() {
        return tmp + "°C";
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public String  getWind(){
        return dir + sc + "级";
    }
}
