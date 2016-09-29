package com.liferyan.coolweather.model;

/**
 * Created by Ryan on 16/9/29.
 */

//天气预报
public class DailyForecast {
    //当地日期
    private String date;
    //WeekDay
    private String weekDay;
    //最高气温
    private String max;
    //最低气温
    private String min;
    //天气状况
    private String cond;

    private StringBuilder stringBuilder = new StringBuilder();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    @Override
    public String toString() {
        stringBuilder.append("      ");
        stringBuilder.append(date);
        stringBuilder.append("  " + weekDay + "  ");
        stringBuilder.append(max).append("°C~");
        stringBuilder.append(min).append("°C   ");
        stringBuilder.append(cond).append(" ");
        return stringBuilder.toString();
    }
}
