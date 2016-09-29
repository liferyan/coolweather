package com.liferyan.coolweather.model;

/**
 * Created by Ryan on 16/9/29.
 */
//生活指数
public class Suggestion {
    //舒适指数
    private String comf;

    //穿衣指数
    private String drsg;

    //感冒指数
    private String flu;

    //运动指数
    private String sport;

    //旅游指数
    private String trav;

    //紫外线指数
    private String uv;

    public String getComf() {
        return comf;
    }

    public void setComf(String comf) {
        this.comf = comf;
    }

    public String getDrsg() {
        return drsg;
    }

    public void setDrsg(String drsg) {
        this.drsg = drsg;
    }

    public String getFlu() {
        return flu;
    }

    public void setFlu(String flu) {
        this.flu = flu;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getTrav() {
        return trav;
    }

    public void setTrav(String trav) {
        this.trav = trav;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }
}
